package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.model.Transaction;
import com.jay.accountmanager.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    
    List<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByAccountPersonIdAndDateBetween(Long personId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.account.person = :person AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumByPersonAndTypeAndDateBetween(
            @Param("person") Person person,
            @Param("type") TransactionType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT t.date FROM Transaction t " +
           "WHERE t.account.person = :person AND t.type = 'INCOME' " +
           "ORDER BY t.date DESC")
    Optional<LocalDateTime> findLastIncomeDate(@Param("person") Person person);

    @Query("SELECT NEW com.jay.accountmanager.dto.CategorySpendingDTO(" +
           "c.name, SUM(t.amount)) " +
           "FROM Transaction t " +
           "JOIN t.category c " +
           "WHERE t.account.person = :person " +
           "AND t.type = 'EXPENSE' " +
           "AND t.date BETWEEN :start AND :end " +
           "GROUP BY c.id, c.name")
    List<com.jay.accountmanager.dto.CategorySpendingDTO> findSpendingByCategory(
            @Param("person") Person person,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t " +
           "WHERE t.account.person = :person " +
           "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountPersonAndDateBetween(
            @Param("person") Person person,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t " +
           "WHERE t.account.person = :person " +
           "ORDER BY t.date DESC")
    List<Transaction> findRecentTransactionsByPerson(
            @Param("person") Person person,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
           "WHERE t.account.person = :person " +
           "ORDER BY t.date DESC")
    org.springframework.data.domain.Page<Transaction> findByAccountPerson(@Param("person") Person person, org.springframework.data.domain.Pageable pageable);
}
