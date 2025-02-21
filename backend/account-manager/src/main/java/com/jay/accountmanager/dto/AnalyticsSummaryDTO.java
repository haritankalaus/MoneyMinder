package com.jay.accountmanager.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalBills;
    private BigDecimal netSavings;
    private double savingsRate;
    private List<CategorySpendingDTO> topCategories;
    private List<MonthlySpendingDTO> monthlySpending;
}
