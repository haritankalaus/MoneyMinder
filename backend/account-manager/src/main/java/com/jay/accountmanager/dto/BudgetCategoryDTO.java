package com.jay.accountmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCategoryDTO {
    private Long id;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;

    @NotNull(message = "Planned amount is required")
    @Positive(message = "Planned amount must be positive")
    private BigDecimal plannedAmount;

    private BigDecimal actualAmount;

    @Builder.Default
    private Boolean isAlertEnabled = false;

    private BigDecimal alertThreshold;
}
