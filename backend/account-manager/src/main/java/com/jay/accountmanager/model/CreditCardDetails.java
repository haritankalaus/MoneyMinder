package com.jay.accountmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_details")
public class CreditCardDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Column(name = "bill_generate_day")
    private Integer billGenerateDay;

    @NotNull
    @Column(name = "payment_due_day")
    private Integer paymentDueDay;

    @NotNull
    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @NotNull
    @Column(name = "late_payment_fee")
    private BigDecimal latePaymentFee;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

}
