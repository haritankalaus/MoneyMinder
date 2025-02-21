package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Payment;
import com.jay.accountmanager.model.Person;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p " +
           "WHERE p.account.person = :person " +
           "AND p.dueDate >= :now " +
           "AND p.status = 'PENDING' " +
           "ORDER BY p.dueDate ASC")
    List<Payment> findUpcomingPaymentsByPerson(
            @Param("person") Person person,
            @Param("now") LocalDateTime now,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Payment p " +
           "WHERE p.account.person = :person " +
           "ORDER BY p.dueDate DESC")
    org.springframework.data.domain.Page<Payment> findByAccountPerson(@Param("person") Person person, org.springframework.data.domain.Pageable pageable);

    List<Payment> findByAccountPersonAndDueDateBetweenAndStatus(
            Person person,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Payment.PaymentStatus status);
}
