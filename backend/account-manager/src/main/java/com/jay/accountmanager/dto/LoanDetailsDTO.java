package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.jay.accountmanager.model.LoanType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetailsDTO {
    private Long id;
    private LoanType loanType;
    private BigDecimal monthlyPayment;
    private Integer paymentDueDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal interestRate;
    private BigDecimal totalLoanAmount;
    private BigDecimal remainingAmount;
}
