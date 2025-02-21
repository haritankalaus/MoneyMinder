package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jay.accountmanager.model.Bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPersonId(Long personId);
    
    @Query("SELECT b FROM Bill b WHERE b.person.id = :personId AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Bill> findByPersonIdAndDueDateBetween(@Param("personId") Long personId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE b.person.id = :personId AND YEAR(b.dueDate) = YEAR(CURRENT_DATE) AND MONTH(b.dueDate) = MONTH(CURRENT_DATE)")
    BigDecimal getMonthlyBills(@Param("personId") Long personId);

    @Query("SELECT e FROM Bill e WHERE e.person.id = :personId AND e.dueDate <= :date ORDER BY e.dueDate ASC")
    List<Bill> findUpcomingBills(@Param("personId") Long personId, @Param("date") LocalDate date);

}
