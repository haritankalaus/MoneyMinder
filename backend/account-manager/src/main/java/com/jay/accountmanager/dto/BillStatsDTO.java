package com.jay.accountmanager.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BillStatsDTO {
    private BigDecimal totalBills;
    private BigDecimal monthlyBills;
    private BigDecimal dueSoon;
    private List<UpcomingPaymentDTO> upcomingPayments;
    private BigDecimal requiredByNextPayday;

    @Data
    public static class UpcomingPaymentDTO {
        private BigDecimal amount;
        private String dueDate;
    }
}
