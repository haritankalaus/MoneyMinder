package com.jay.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jay.security.models.User;
import com.jay.security.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Component
@Builder
@AllArgsConstructor
public class AuthenticationFacade {

	private final UserRepository userRepository;
	
	public UserDetailsImpl getLoggedinUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserDetailsImpl)auth.getPrincipal();
	}
	
	public Long getLoggedinUserId() {
		try {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null && auth.getPrincipal() != null && auth.getPrincipal() instanceof com.jay.security.services.UserDetailsImpl)
			return ((UserDetailsImpl)auth.getPrincipal()).getId();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}
	
	public User getCurrentUser() {
		return userRepository.findById(getLoggedinUserId()).get();
	}
	
}
