package com.jay.security.payload.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  
	private HttpStatus status;
	
	private Long userId;
	
	private JwtResponse jwtResponse;
	
	private String message;
	
	public AuthResponse(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public AuthResponse(HttpStatus status, Long userId, JwtResponse jwtResponse) {
		this.status = status;
		this.userId = userId;
		this.jwtResponse = jwtResponse;
	}

}

