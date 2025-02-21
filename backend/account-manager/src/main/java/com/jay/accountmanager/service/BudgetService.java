package com.jay.accountmanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.BudgetCategoryDTO;
import com.jay.accountmanager.dto.BudgetDTO;
import com.jay.accountmanager.dto.BudgetSummaryDTO;
import com.jay.accountmanager.model.Budget;
import com.jay.accountmanager.model.BudgetCategory;
import com.jay.accountmanager.model.BudgetStatus;
import com.jay.accountmanager.model.Category;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.model.Transaction;
import com.jay.accountmanager.model.TransactionType;
import com.jay.accountmanager.repository.BudgetCategoryRepository;
import com.jay.accountmanager.repository.BudgetRepository;
import com.jay.accountmanager.repository.CategoryRepository;
import com.jay.accountmanager.repository.PersonRepository;
import com.jay.accountmanager.repository.TransactionRepository;
import com.jay.security.services.AuthenticationFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PersonRepository personRepository;

    @Transactional
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        
        if (budgetRepository.findByPersonIdAndPeriod(authenticationFacade.getLoggedinUserId(), budgetDTO.getPeriod()).isPresent()) {
            throw new RuntimeException("Budget already exists for this period");
        }

        //User user = userRepository.findById(authenticationFacade.getLoggedinUserId()).get();
        Person person = personRepository.findById(authenticationFacade.getLoggedinUserId()).get();
        
        Budget budget = Budget.builder()
                .period(budgetDTO.getPeriod())
                .totalBudget(budgetDTO.getTotalBudget())
                .status(BudgetStatus.ACTIVE)
                .person(person)
                .build();

        Budget savedBudget = budgetRepository.save(budget);

        // Create budget categories
        List<BudgetCategory> categories = budgetDTO.getCategories().stream()
                .map(categoryDTO -> createBudgetCategory(savedBudget, categoryDTO))
                .collect(Collectors.toList());

        savedBudget.setCategories(categories);
        return mapToDTO(savedBudget);
    }

    private BudgetCategory createBudgetCategory(Budget budget, BudgetCategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return BudgetCategory.builder()
                .budget(budget)
                .category(category)
                .plannedAmount(categoryDTO.getPlannedAmount())
                .actualAmount(BigDecimal.ZERO)
                .isAlertEnabled(categoryDTO.getIsAlertEnabled())
                .alertThreshold(categoryDTO.getAlertThreshold())
                .build();
    }

    public BudgetDTO getCurrentBudget() {
        YearMonth currentPeriod = YearMonth.now();
        
        Budget budget = budgetRepository.findByPersonIdAndPeriod(authenticationFacade.getLoggedinUserId(), currentPeriod)
                .orElseThrow(() -> new RuntimeException("No budget found for current period"));
                
        updateActualAmounts(budget);
        return mapToDTO(budget);
    }

    @Transactional
    public void updateActualAmounts(Budget budget) {
        LocalDateTime startDate = budget.getPeriod().atDay(1).atStartOfDay();
        LocalDateTime endDate = budget.getPeriod().atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository
                .findByAccountPersonIdAndDateBetween(authenticationFacade.getLoggedinUserId(), startDate, endDate);

        Map<Long, BigDecimal> categoryTotals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                    t -> t.getCategory().getId(),
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        budget.getCategories().forEach(bc -> {
            BigDecimal actual = categoryTotals.getOrDefault(bc.getCategory().getId(), BigDecimal.ZERO);
            bc.setActualAmount(actual);
        });

        budgetRepository.save(budget);
    }

    public BudgetSummaryDTO getBudgetSummary(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        updateActualAmounts(budget);

        BigDecimal totalPlanned = budget.getCategories().stream()
                .map(BudgetCategory::getPlannedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActual = budget.getCategories().stream()
                .map(BudgetCategory::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingBudget = totalPlanned.subtract(totalActual);
        
        double spendingPercentage = totalActual.divide(totalPlanned, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        List<BudgetCategory> overspentCategories = budgetCategoryRepository
                .findOverspentCategories(budgetId);

        return BudgetSummaryDTO.builder()
                .totalBudget(totalPlanned)
                .totalSpent(totalActual)
                .remainingBudget(remainingBudget)
                .spendingPercentage(spendingPercentage)
                .overspentCategories(overspentCategories.stream()
                        .map(this::mapCategoryToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private BudgetDTO mapToDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getId())
                .period(budget.getPeriod())
                .totalBudget(budget.getTotalBudget())
                .status(budget.getStatus())
                .categories(budget.getCategories().stream()
                        .map(this::mapCategoryToDTO)
                        .collect(Collectors.toList()))
                .notes(budget.getNotes())
                .build();
    }

    private BudgetCategoryDTO mapCategoryToDTO(BudgetCategory category) {
        return BudgetCategoryDTO.builder()
                .id(category.getId())
                .categoryId(category.getCategory().getId())
                .categoryName(category.getCategory().getName())
                .plannedAmount(category.getPlannedAmount())
                .actualAmount(category.getActualAmount())
                .isAlertEnabled(category.getIsAlertEnabled())
                .alertThreshold(category.getAlertThreshold())
                .build();
    }
}
