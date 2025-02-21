package com.jay.accountmanager.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final Map<String, OtpData> otpMap = new ConcurrentHashMap<>();
    private static final int OTP_VALID_DURATION = 5; // 5 minutes
    
    @Data
    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;
    }
    
    public String generateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpMap.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(OTP_VALID_DURATION)));
        return otp;
    }
    
    public boolean validateOTP(String email, String otp) {
        OtpData otpData = otpMap.get(email);
        if (otpData == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            otpMap.remove(email);
            return false;
        }
        
        if (!otpData.getOtp().equals(otp)) {
            return false;
        }
        
        otpMap.remove(email);
        return true;
    }
}
