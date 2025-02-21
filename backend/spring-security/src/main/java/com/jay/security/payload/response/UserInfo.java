package com.jay.security.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

	  private String username;

	  private String firstName;
	  
	  private String lastName;
	  
	  private String email;
	  
	  private List<String> roles;
	  

}

