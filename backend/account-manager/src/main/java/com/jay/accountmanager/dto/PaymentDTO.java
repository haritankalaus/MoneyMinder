package com.jay.accountmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jay.accountmanager.model.Payment.PaymentStatus;
import com.jay.accountmanager.model.Payment.RecurrenceType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    @NotNull(message = "Recurrence type is required")
    private RecurrenceType recurrence;

    private String description;

    @NotNull(message = "Account ID is required")
    private Long accountId;

    private Long categoryId;
    
    private String categoryName;
}
