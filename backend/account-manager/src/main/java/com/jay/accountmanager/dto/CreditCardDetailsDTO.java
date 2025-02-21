package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDetailsDTO {
    private Long id;
    private Integer billGenerateDay;
    private Integer paymentDueDay;
    private BigDecimal interestRate;
    private BigDecimal latePaymentFee;
    private BigDecimal creditLimit;
}
