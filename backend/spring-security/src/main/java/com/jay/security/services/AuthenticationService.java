package com.jay.security.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.jay.security.models.ERole;
import com.jay.security.models.RefreshToken;
import com.jay.security.models.Role;
import com.jay.security.models.User;
import com.jay.security.exception.TokenRefreshException;
import com.jay.security.jwt.JwtUtils;
import com.jay.security.payload.request.LoginRequest;
import com.jay.security.payload.request.SignupRequest;
import com.jay.security.payload.request.TokenRefreshRequest;
import com.jay.security.payload.response.AuthResponse;
import com.jay.security.payload.response.JwtResponse;
import com.jay.security.payload.response.TokenRefreshResponse;
import com.jay.security.payload.response.UserInfo;
import com.jay.security.repository.RoleRepository;
import com.jay.security.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
	
  private final AuthenticationManager authenticationManager;
  
  private final UserRepository userRepository;
  
  private final RoleRepository roleRepository;
  
  private final PasswordEncoder encoder;
  
  private final JwtUtils jwtUtils;

  private final RefreshTokenService refreshTokenService;


  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Authentication attempt for user: {}", loginRequest.getUsername());
    
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    // Create new refresh token
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    log.info("User {} successfully authenticated", loginRequest.getUsername());
    return ResponseEntity.ok(new JwtResponse(jwt, 
                                         refreshToken.getToken(),
                                         new UserInfo(
                                           userDetails.getUsername(), 
                                           userDetails.getFirstName(),
                                           userDetails.getLastName(),
                                           userDetails.getEmail(), 
                                           roles)));
  }

  public AuthResponse registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    log.info("Processing registration request for user: {}", signUpRequest.getEmail());
    
    if (userRepository.existsByUsername(signUpRequest.getEmail())) {
      log.warn("Registration failed - username already exists: {}", signUpRequest.getEmail());
      return 
    		  new AuthResponse(HttpStatus.BAD_REQUEST,"Error: Username is already taken!");      
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      log.warn("Registration failed - email already exists: {}", signUpRequest.getEmail());
      return 
    		  new AuthResponse(HttpStatus.BAD_REQUEST,"Error: Email is already in use!");
    }

    // Validate password complexity
    if (!isPasswordValid(signUpRequest.getPassword())) {
      log.warn("Registration failed - password does not meet complexity requirements");
      return 
    		  new AuthResponse(HttpStatus.BAD_REQUEST,"Error: Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
    }


    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);
          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }
    // Create new user's account
    User user = new User(signUpRequest.getEmail(),
                        signUpRequest.getEmail(),
                        encoder.encode(signUpRequest.getPassword()));
    user.setFirstName(signUpRequest.getFirstName());
    user.setLastName(signUpRequest.getLastName());
    
    user.setRoles(roles);
    user = userRepository.saveAndFlush(user);

    // Create person record in account-manager
    try {
        Map<String, Object> personRequest = new HashMap<>();
        personRequest.put("userId", user.getId());
        personRequest.put("firstName", user.getFirstName());
        personRequest.put("lastName", user.getLastName());
        personRequest.put("email", user.getEmail());
        personRequest.put("language", "en");
        personRequest.put("currency", "USD");
        personRequest.put("notifications", true);
        
        log.info("Person record created for user: {}", user.getEmail());
    } catch (Exception e) {
        log.error("Failed to create person record: {}", e.getMessage());
        // Continue with user creation even if person creation fails
    }

    // Generate JWT and refresh token
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> userRoles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    // Create refresh token
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    log.info("User {} successfully registered", signUpRequest.getEmail());
    
    return new AuthResponse(HttpStatus.OK,userDetails.getId(),new JwtResponse(jwt, 
                                           refreshToken.getToken(),
                                           new UserInfo(
                                             userDetails.getUsername(), 
                                             userDetails.getFirstName(),
                                             userDetails.getLastName(),
                                             userDetails.getEmail(), 
                                             userRoles)));
  }

  public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    try {
      return refreshTokenService.findByToken(requestRefreshToken)
          .map(refreshTokenService::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(user -> {
            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
          })
          .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
              "Refresh token is not in database!"));
    } catch (TokenRefreshException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  private boolean isPasswordValid(String password) {
    // Implement password complexity validation logic here
    // For example:
    return password.length() >= 8
        && password.matches(".*[a-z].*")
        && password.matches(".*[A-Z].*")
        && password.matches(".*\\d.*")
        && password.matches(".*[^a-zA-Z0-9].*");
  }
}

