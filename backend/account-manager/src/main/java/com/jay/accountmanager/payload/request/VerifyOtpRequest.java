package com.jay.accountmanager.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String otp;
    
    @NotBlank
    private String newPassword;
}
