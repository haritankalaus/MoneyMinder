package com.jay.accountmanager.service;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.IncomeRequest;
import com.jay.accountmanager.dto.IncomeStatsDTO;
import com.jay.accountmanager.model.Income;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.repository.IncomeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final PersonService personService;

    @Transactional
    public Income createIncome(Long userId, IncomeRequest request) {
        Person person = personService.getPerson(userId);

        Income income = new Income();
        updateIncomeFromRequest(income, request);
        income.setPerson(person);

        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomes(Long userId) {
    	Person person = personService.getPerson(userId);
        return incomeRepository.findByPersonIdOrderByNextDueDateAsc(person.getId());
    }

    public Income getIncome(Long userId, Long incomeId) {
    	Person person = personService.getPerson(userId);
        return incomeRepository.findById(incomeId)
                .filter(income -> income.getPerson().getId().equals(person.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Income not found"));
    }

    @Transactional
    public Income updateIncome(Long userId, Long incomeId, IncomeRequest request) {
        Income income = getIncome(userId, incomeId);
        updateIncomeFromRequest(income, request);
        return incomeRepository.save(income);
    }

    @Transactional
    public void deleteIncome(Long userId, Long incomeId) {
        Income income = getIncome(userId, incomeId);
        incomeRepository.delete(income);
    }

    private void updateIncomeFromRequest(Income income, IncomeRequest request) {
        income.setName(request.getName());
        income.setAmount(request.getAmount());
        income.setType(request.getType());
        income.setRecurrenceType(request.getRecurrenceType());
        income.setRecurrenceDay(request.getRecurrenceDay());
        income.setNextDueDate(request.getNextDueDate());
        income.setCurrency(request.getCurrency());
        income.setDescription(request.getDescription());
    }

    public IncomeStatsDTO getIncomeStats(Long userId) {
        //List<Income> activeIncomes = incomeRepository.findByPersonIdAndIsActiveTrue(userId);
        List<Income> upcomingIncomes = incomeRepository.findByPersonIdOrderByNextDueDateAsc(userId);
        
        BigDecimal monthlyIncome = upcomingIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashInHand = calculateCashInHand(upcomingIncomes);
        BigDecimal receivedToday = calculateReceivedToday(upcomingIncomes);
        
        IncomeStatsDTO.UpcomingIncome upcomingIncome = calculateUpcomingIncome(upcomingIncomes);

        return IncomeStatsDTO.builder()
                .cashInHand(cashInHand)
                .monthlyIncome(monthlyIncome)
                .receivedToday(receivedToday)
                .upcomingIncome(upcomingIncome)
                .build();
    }

    private BigDecimal calculateCashInHand(List<Income> incomes) {
        return incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateReceivedToday(List<Income> incomes) {
        LocalDate today = LocalDate.now();
        return incomes.stream()
                .filter(income -> income.getNextDueDate().equals(today))
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private IncomeStatsDTO.UpcomingIncome calculateUpcomingIncome(List<Income> incomes) {
        LocalDate today = LocalDate.now();
        return incomes.stream()
                .filter(income -> income.getNextDueDate().isAfter(today))
                .findFirst()
                .map(income -> IncomeStatsDTO.UpcomingIncome.builder()
                        .amount(income.getAmount())
                        .nextDate(income.getNextDueDate())
                        .build())
                .orElse(IncomeStatsDTO.UpcomingIncome.builder()
                        .amount(BigDecimal.ZERO)
                        .nextDate(null)
                        .build());
    }
}
