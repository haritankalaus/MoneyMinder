package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    private DashboardStatsDTO stats;
    private List<CategorySpendingDTO> spendingByCategory;
    private List<PaymentDTO> upcomingPayments;
    private List<TransactionDTO> recentTransactions;
    private List<MonthlySpendingDTO> monthlyData;

    @Data
    public static class SpendingByCategoryDTO {
        private String name;
        private String color;
        private String icon;
        private BigDecimal amount;
    }

    @Data
    public static class MonthlyDataDTO {
        private String month;
        private BigDecimal income;
        private BigDecimal bills;
    }
}
