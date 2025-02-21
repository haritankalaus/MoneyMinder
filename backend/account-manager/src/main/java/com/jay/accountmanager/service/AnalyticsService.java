package com.jay.accountmanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.AnalyticsSummaryDTO;
import com.jay.accountmanager.dto.CategorySpendingDTO;
import com.jay.accountmanager.dto.MonthlySpendingDTO;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.model.Transaction;
import com.jay.accountmanager.model.TransactionType;
import com.jay.accountmanager.repository.PersonRepository;
import com.jay.accountmanager.repository.TransactionRepository;
import com.jay.security.services.AuthenticationFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final TransactionRepository transactionRepository;
    private final PersonRepository personRepository;
    private final AuthenticationFacade authenticationFacade;

    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getAnalyticsSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Person person = personRepository.findByUserId(authenticationFacade.getLoggedinUserId())
            .orElseThrow(() -> new RuntimeException("Person not found"));

        List<Transaction> transactions = transactionRepository
                .findByAccountPersonAndDateBetween(person, startDate, endDate);

        BigDecimal totalIncome = calculateTotal(transactions, TransactionType.INCOME);
        BigDecimal totalBills = calculateTotal(transactions, TransactionType.EXPENSE);
        BigDecimal netSavings = totalIncome.subtract(totalBills);
        
        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netSavings.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue()
                : 0.0;

        List<CategorySpendingDTO> topCategories = getTopCategoriesBySpending(transactions, 5);
        List<MonthlySpendingDTO> monthlySpending = getMonthlySpending(transactions);

        return AnalyticsSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalBills(totalBills)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .topCategories(topCategories)
                .monthlySpending(monthlySpending)
                .build();
    }

    private BigDecimal calculateTotal(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CategorySpendingDTO> getTopCategoriesBySpending(List<Transaction> transactions, int limit) {
        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                    t -> t.getCategory().getName(),
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        BigDecimal totalSpending = categoryTotals.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryTotals.entrySet().stream()
                .map(entry -> CategorySpendingDTO.builder()
                        .categoryName(entry.getKey())
                        .amount(entry.getValue())
                        .percentage(calculatePercentage(entry.getValue(), totalSpending))
                        .build())
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculatePercentage(BigDecimal amount, BigDecimal total) {
        return total.compareTo(BigDecimal.ZERO) > 0
                ? amount.divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue()
                : 0.0;
    }

    private List<MonthlySpendingDTO> getMonthlySpending(List<Transaction> transactions) {
        List<MonthlySpendingDTO> monthlySpending = new ArrayList<>();
        YearMonth current = YearMonth.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        // Get last 12 months
        for (int i = 11; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
            
            List<Transaction> monthTransactions = transactions.stream()
                    .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                    .collect(Collectors.toList());
            
            BigDecimal income = calculateTotal(monthTransactions, TransactionType.INCOME);
            BigDecimal bills = calculateTotal(monthTransactions, TransactionType.EXPENSE);
            
            monthlySpending.add(MonthlySpendingDTO.builder()
                    .month(month.format(formatter))
                    .income(income)
                    .bills(bills)
                    .build());
        }
        
        return monthlySpending;
    }
}
