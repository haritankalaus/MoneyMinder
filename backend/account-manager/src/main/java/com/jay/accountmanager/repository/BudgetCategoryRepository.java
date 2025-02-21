package com.jay.accountmanager.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.BudgetCategory;
import com.jay.accountmanager.model.Person;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
    List<BudgetCategory> findByBudgetId(Long budgetId);
    
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.id = :budgetId AND bc.actualAmount > bc.plannedAmount")
    List<BudgetCategory> findOverspentCategories(Long budgetId);
    
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.isAlertEnabled = true AND " +
           "bc.actualAmount >= (bc.plannedAmount * bc.alertThreshold)")
    List<BudgetCategory> findCategoriesNearingLimit();
    
    @Query("SELECT bc FROM BudgetCategory bc " +
           "WHERE bc.budget.person = :person " +
           "AND bc.budget.period = :period " +
           "ORDER BY bc.actualAmount DESC")
    List<BudgetCategory> findTopSpendingCategoriesForPeriod(
            @Param("person") Person person,
            @Param("period") YearMonth period);
    
    @Query("SELECT bc FROM BudgetCategory bc " +
           "WHERE bc.budget.person = :person " +
           "AND bc.budget.period BETWEEN :startPeriod AND :endPeriod " +
           "AND bc.actualAmount > bc.plannedAmount " +
           "ORDER BY (bc.actualAmount - bc.plannedAmount) DESC")
    List<BudgetCategory> findMostOverspentCategories(
            @Param("person") Person person,
            @Param("startPeriod") YearMonth startPeriod,
            @Param("endPeriod") YearMonth endPeriod);
    
    @Query("SELECT NEW com.jay.accountmanager.dto.CategorySpendingDTO(" +
           "bc.category.name, AVG(bc.actualAmount)) " +
           "FROM BudgetCategory bc " +
           "WHERE bc.budget.person = :person " +
           "AND bc.budget.period BETWEEN :startPeriod AND :endPeriod " +
           "GROUP BY bc.category.id, bc.category.name " +
           "ORDER BY AVG(bc.actualAmount) DESC")
    List<com.jay.accountmanager.dto.CategorySpendingDTO> findAverageSpendingByCategory(
            @Param("person") Person person,
            @Param("startPeriod") YearMonth startPeriod,
            @Param("endPeriod") YearMonth endPeriod);
}
