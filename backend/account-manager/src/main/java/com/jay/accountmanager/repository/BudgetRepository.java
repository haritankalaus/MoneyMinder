package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Budget;
import com.jay.accountmanager.model.Person;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByPersonId(Long personId);
    
    List<Budget> findByPerson(Person person);
    
    Optional<Budget> findByPersonIdAndPeriod(Long personId, YearMonth period);
    
    Optional<Budget> findByPersonAndPeriod(Person person, YearMonth period);
    
    List<Budget> findByPersonIdAndPeriodBetween(Long personId, YearMonth startPeriod, YearMonth endPeriod);
    
    List<Budget> findByPersonAndPeriodBetween(Person person, YearMonth startPeriod, YearMonth endPeriod);
    
    @Query("SELECT b FROM Budget b WHERE b.person = :person " +
           "AND b.period <= :currentPeriod " +
           "ORDER BY b.period DESC")
    List<Budget> findRecentBudgets(
            @Param("person") Person person,
            @Param("currentPeriod") YearMonth currentPeriod);
    
    @Query("SELECT AVG(b.totalBudget) FROM Budget b " +
           "WHERE b.person = :person " +
           "AND b.period BETWEEN :startPeriod AND :endPeriod")
    BigDecimal calculateAverageBudget(
            @Param("person") Person person,
            @Param("startPeriod") YearMonth startPeriod,
            @Param("endPeriod") YearMonth endPeriod);
    
    @Query("SELECT b FROM Budget b " +
           "WHERE b.person = :person " +
           "AND b.totalActual > b.totalBudget " +
           "AND b.period BETWEEN :startPeriod AND :endPeriod")
    List<Budget> findOverspentBudgets(
            @Param("person") Person person,
            @Param("startPeriod") YearMonth startPeriod,
            @Param("endPeriod") YearMonth endPeriod);
}
