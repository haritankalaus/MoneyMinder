package com.jay.accountmanager.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.jay.accountmanager.model.IncomeType;
import com.jay.accountmanager.model.RecurrenceType;

@Data
public class IncomeRequest {
    @NotNull
    private String name;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private IncomeType type;

    @NotNull
    private RecurrenceType recurrenceType;

    private Integer recurrenceDay;
    private LocalDate nextDueDate;
    
    @NotNull
    private String currency;
    
    private String description;
}
