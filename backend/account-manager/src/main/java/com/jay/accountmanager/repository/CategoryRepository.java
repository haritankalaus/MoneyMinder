package com.jay.accountmanager.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Category;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.model.TransactionType;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByPersonId(Long personId);
    
    List<Category> findByPerson(Person person);
    
    List<Category> findByPersonIdAndType(Long personId, TransactionType type);
    
    List<Category> findByPersonAndType(Person person, TransactionType type);
    
    boolean existsByNameAndPersonId(String name, Long personId);
    
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.transactions " +
           "WHERE c.person = :person AND c.type = 'EXPENSE' " +
           "ORDER BY SIZE(c.transactions) DESC")
    List<Category> findMostUsedCategories(@Param("person") Person person);
    
    @Query("SELECT c FROM Category c " +
           "WHERE c.person = :person AND c.type = :type " +
           "AND EXISTS (SELECT t FROM Transaction t WHERE t.category = c AND t.date BETWEEN :start AND :end)")
    List<Category> findActiveCategories(
            @Param("person") Person person,
            @Param("type") TransactionType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT NEW com.jay.accountmanager.dto.CategorySpendingDTO(" +
           "c.name, SUM(t.amount)) " +
           "FROM Category c " +
           "JOIN c.transactions t " +
           "WHERE c.person = :person AND c.type = 'EXPENSE' " +
           "AND t.date BETWEEN :start AND :end " +
           "GROUP BY c.id, c.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<com.jay.accountmanager.dto.CategorySpendingDTO> findTopSpendingCategories(
            @Param("person") Person person,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
