package com.jay.accountmanager.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CreatePersonRequest {
    @NotNull
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String language;
    private String currency;
    private boolean notifications;
}
