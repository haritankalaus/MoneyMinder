package com.jay.accountmanager.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class IncomeStatsDTO {
    private BigDecimal cashInHand;
    private BigDecimal monthlyIncome;
    private BigDecimal receivedToday;
    private UpcomingIncome upcomingIncome;

    @Data
    @Builder
    public static class UpcomingIncome {
        private BigDecimal amount;
        private LocalDate nextDate;
    }
}
