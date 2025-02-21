package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal accountBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyBills;
    private BigDecimal savingsGoal;
    private double savingsProgress;
    private LocalDateTime nextPaydate;
    private int daysLeftInMonth;
}
