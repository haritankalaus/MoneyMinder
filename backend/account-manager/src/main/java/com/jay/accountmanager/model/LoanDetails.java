package com.jay.accountmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_details")
public class LoanDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type")
    private LoanType loanType;

    @NotNull
    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @NotNull
    @Column(name = "payment_due_day")
    private Integer paymentDueDay;

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "total_loan_amount")
    private BigDecimal totalLoanAmount;

    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;
}
