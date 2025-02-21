package com.jay.accountmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import com.jay.accountmanager.model.BudgetStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long id;

    @NotNull(message = "Period is required")
    private YearMonth period;

    @NotNull(message = "Total budget is required")
    private BigDecimal totalBudget;

    private BudgetStatus status;

    private List<BudgetCategoryDTO> categories;

    private String notes;
}
