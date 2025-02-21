package com.jay.accountmanager.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.CategorySpendingDTO;
import com.jay.accountmanager.dto.DashboardResponseDTO;
import com.jay.accountmanager.dto.DashboardStatsDTO;
import com.jay.accountmanager.dto.MonthlySpendingDTO;
import com.jay.accountmanager.dto.PaymentDTO;
import com.jay.accountmanager.dto.TransactionDTO;
import com.jay.accountmanager.model.Payment;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.model.Transaction;
import com.jay.accountmanager.model.TransactionType;
import com.jay.accountmanager.repository.AccountRepository;
import com.jay.accountmanager.repository.PaymentRepository;
import com.jay.accountmanager.repository.PersonRepository;
import com.jay.accountmanager.repository.TransactionRepository;
import com.jay.security.models.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;

    private Person getPersonFromUser(User user) {
        if (user == null) {
            throw new SecurityException("No authenticated user found");
        }
        return personRepository.findByUserId(user.getId())
            .orElseThrow(() -> new SecurityException("Person not found for user: " + user.getUsername()));
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardData(User user) {
        Person person = getPersonFromUser(user);
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        return DashboardResponseDTO.builder()
            .stats(calculateStats(person, monthStart, monthEnd))
            .spendingByCategory(getSpendingByCategory(person, monthStart, monthEnd))
            .upcomingPayments(getUpcomingPayments(person, now))
            .recentTransactions(getRecentTransactions(person))
            .monthlyData(getMonthlyData(person))
            .build();
    }

    private DashboardStatsDTO calculateStats(Person person, LocalDateTime monthStart, LocalDateTime monthEnd) {
        // Get total balance across all accounts
        BigDecimal accountBalance = accountRepository.getTotalBalance(person);
        if (accountBalance == null) {
            accountBalance = BigDecimal.ZERO;
        }

        // Monthly Income
        BigDecimal monthlyIncome = transactionRepository.sumByPersonAndTypeAndDateBetween(
            person, TransactionType.INCOME, monthStart, monthEnd);

        // Monthly Bills
        BigDecimal monthlyBills = transactionRepository.sumByPersonAndTypeAndDateBetween(
            person, TransactionType.EXPENSE, monthStart, monthEnd);

        // Calculate savings progress
        double savingsProgress = 0.0;
        if (person.getSavingsGoal() != null && person.getSavingsGoal().compareTo(BigDecimal.ZERO) > 0) {
            savingsProgress = accountBalance.divide(person.getSavingsGoal(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        }

        return DashboardStatsDTO.builder()
            .accountBalance(accountBalance)
            .monthlyIncome(monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO)
            .monthlyBills(monthlyBills != null ? monthlyBills : BigDecimal.ZERO)
            .savingsGoal(person.getSavingsGoal())
            .savingsProgress(savingsProgress)
            .nextPaydate(calculateNextPaydate(person))
            .daysLeftInMonth(monthEnd.getDayOfMonth() - LocalDateTime.now().getDayOfMonth())
            .build();
    }

    private LocalDateTime calculateNextPaydate(Person person) {
        return transactionRepository.findLastIncomeDate(person)
            .map(lastDate -> {
                YearMonth nextMonth = YearMonth.from(lastDate).plusMonths(1);
                return nextMonth.atDay(lastDate.getDayOfMonth()).atStartOfDay();
            })
            .orElseGet(() -> LocalDateTime.now().plusDays(14));
    }

    private List<CategorySpendingDTO> getSpendingByCategory(Person person, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findSpendingByCategory(person, start, end);
    }

    private List<PaymentDTO> getUpcomingPayments(Person person, LocalDateTime now) {
        return paymentRepository.findUpcomingPaymentsByPerson(person, now, Pageable.ofSize(5)).stream()
            .map(payment -> PaymentDTO.builder()
                .id(payment.getId())
                .title(payment.getTitle())
                .amount(payment.getAmount())
                .dueDate(payment.getDueDate())
                .status(payment.getStatus())
                .recurrence(payment.getRecurrence())
                .description(payment.getDescription())
                .accountId(payment.getAccount().getId())
                .categoryId(payment.getCategory() != null ? payment.getCategory().getId() : null)
                .categoryName(payment.getCategory() != null ? payment.getCategory().getName() : null)
                .build())
            .collect(Collectors.toList());
    }

    private List<TransactionDTO> getRecentTransactions(Person person) {
        return transactionRepository.findRecentTransactionsByPerson(person, Pageable.ofSize(5)).stream()
            .map(transaction -> TransactionDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .notes(transaction.getNotes())
                .build())
            .collect(Collectors.toList());
    }

    private List<MonthlySpendingDTO> getMonthlyData(Person person) {
        List<MonthlySpendingDTO> monthlyData = new ArrayList<>();
        YearMonth current = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

            BigDecimal income = transactionRepository.sumByPersonAndTypeAndDateBetween(
                person, TransactionType.INCOME, start, end);
            BigDecimal bills = transactionRepository.sumByPersonAndTypeAndDateBetween(
                person, TransactionType.EXPENSE, start, end);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            String formattedMonth = month.format(formatter);

            monthlyData.add(MonthlySpendingDTO.builder()
                .month(formattedMonth)
                .income(income != null ? income : BigDecimal.ZERO)
                .bills(bills != null ? bills : BigDecimal.ZERO)
                .build());
        }

        return monthlyData;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactions(User user, Pageable pageable) {
        Person person = getPersonFromUser(user);
        Page<Transaction> transactions = transactionRepository.findByAccountPerson(person, pageable);
        
        List<TransactionDTO> dtos = transactions.getContent().stream()
            .map(transaction -> TransactionDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .notes(transaction.getNotes())
                .build())
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, transactions.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> getPayments(User user, Pageable pageable) {
        Person person = getPersonFromUser(user);
        Page<Payment> payments = paymentRepository.findByAccountPerson(person, pageable);
        
        List<PaymentDTO> dtos = payments.getContent().stream()
            .map(payment -> PaymentDTO.builder()
                .id(payment.getId())
                .title(payment.getTitle())
                .amount(payment.getAmount())
                .dueDate(payment.getDueDate())
                .status(payment.getStatus())
                .recurrence(payment.getRecurrence())
                .description(payment.getDescription())
                .accountId(payment.getAccount().getId())
                .categoryId(payment.getCategory() != null ? payment.getCategory().getId() : null)
                .categoryName(payment.getCategory() != null ? payment.getCategory().getName() : null)
                .build())
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, payments.getTotalElements());
    }
}
