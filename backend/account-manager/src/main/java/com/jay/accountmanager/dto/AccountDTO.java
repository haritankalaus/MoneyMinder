package com.jay.accountmanager.dto;

import java.math.BigDecimal;

import com.jay.accountmanager.model.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;

    @NotBlank(message = "Account name is required")
    private String name;

    @NotBlank(message = "Account number is required")
    //@Pattern(regexp = "^[0-9]{8,17}$", message = "Invalid account number format")
    private String accountNumber;

    @NotNull(message = "Initial balance is required")
    private BigDecimal balance;

    @NotNull(message = "Account type is required")
    private AccountType type;

    private CreditCardDetailsDTO creditCardDetails;
    
    private LoanDetailsDTO loanDetails;
}
