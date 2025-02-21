package com.jay.accountmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.payload.request.ForgotPasswordRequest;
import com.jay.accountmanager.payload.request.VerifyOtpRequest;
import com.jay.accountmanager.repository.PersonRepository;
import com.jay.accountmanager.service.EmailService;
import com.jay.accountmanager.service.OtpService;
import com.jay.security.models.User;
import com.jay.security.services.AuthenticationService;
import com.jay.security.payload.request.LoginRequest;
import com.jay.security.payload.request.SignupRequest;
import com.jay.security.payload.request.TokenRefreshRequest;
import com.jay.security.payload.response.AuthResponse;
import com.jay.security.payload.response.MessageResponse;
import com.jay.security.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;
  private final PersonRepository personRepository;
  private final OtpService otpService;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Authentication attempt for user: {}", loginRequest.getUsername());
    return authenticationService.authenticateUser(loginRequest);
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    log.info("Processing registration request for user: {}", signUpRequest.getEmail());
    AuthResponse response = authenticationService.registerUser(signUpRequest);
    
    if(response.getStatus() != HttpStatus.OK) {
	    return ResponseEntity
	            .badRequest()
	            .body(new MessageResponse(response.getMessage()));
    }
    
    User user = userRepository.findById(response.getUserId()).get();
    Person person = Person.builder().user(user).build();
    personRepository.saveAndFlush(person);
    
    return ResponseEntity.ok(response.getJwtResponse());
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
	  return authenticationService.refreshToken(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    log.info("Processing logout request");
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      SecurityContextHolder.clearContext();
      log.info("User successfully logged out");
      return ResponseEntity.ok(new MessageResponse("User logged out successfully"));
    }
    
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new MessageResponse("No active session found"));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    log.info("Processing forgot password request for email: {}", request.getEmail());
    
    User user = userRepository.findByEmail(request.getEmail())
        .orElse(null);
    
    if (user == null) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("User not found with this email"));
    }
    
    String otp = otpService.generateOTP(request.getEmail());
    emailService.sendOtpEmail(request.getEmail(), otp);
    
    return ResponseEntity.ok(new MessageResponse("OTP has been sent to your email"));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
    log.info("Processing OTP verification for email: {}", request.getEmail());
    
    if (!otpService.validateOTP(request.getEmail(), request.getOtp())) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("Invalid or expired OTP"));
    }
    
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    
    return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
  }
}
