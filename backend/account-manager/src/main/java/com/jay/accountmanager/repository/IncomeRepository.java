package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jay.accountmanager.model.Income;

import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    
    List<Income> findByPersonIdOrderByNextDueDateAsc(Long personId);
}
