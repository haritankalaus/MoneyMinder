package com.jay.security.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jay.security.models.ERole;
import com.jay.security.models.Role;
import com.jay.security.repository.RoleRepository;

@Configuration
public class RoleInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Only initialize if roles table is empty
            if (roleRepository.count() == 0) {
                Role userRole = new Role();
                userRole.setName(ERole.ROLE_USER);
                roleRepository.save(userRole);

                Role modRole = new Role();
                modRole.setName(ERole.ROLE_MODERATOR);
                roleRepository.save(modRole);

                Role adminRole = new Role();
                adminRole.setName(ERole.ROLE_ADMIN);
                roleRepository.save(adminRole);
            }
        };
    }
}

