package com.jay.accountmanager.repository;

import com.jay.accountmanager.model.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
    List<ExpenseType> findAllByCategory(String category);
    List<ExpenseType> findByActiveTrue();
    
    //@Query("SELECT et FROM ExpenseType et WHERE UPPER(et.category) = UPPER(:category)")
    Optional<ExpenseType> findByCategory(String category);
}
