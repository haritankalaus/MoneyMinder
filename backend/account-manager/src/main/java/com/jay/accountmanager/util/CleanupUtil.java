package com.jay.accountmanager.util;

import java.io.IOException;
import java.nio.file.*;

public class CleanupUtil {
    public static void main(String[] args) throws IOException {
        String baseDir = "c:/Data/codium/backend/src/main/java/com/finance/accountmanager";
        
        // Delete security files
        Files.deleteIfExists(Paths.get(baseDir, "security", "JwtAuthenticationFilter.java"));
        Files.deleteIfExists(Paths.get(baseDir, "security", "JwtService.java"));
        Files.deleteIfExists(Paths.get(baseDir, "controller", "AuthenticationController.java"));
        Files.deleteIfExists(Paths.get(baseDir, "service", "AuthenticationService.java"));
        
        // Delete auth DTOs
        String dtoDir = baseDir + "/dto";
        Files.deleteIfExists(Paths.get(dtoDir, "AuthResponseDTO.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "AuthenticationRequest.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "AuthenticationResponse.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "ChangePasswordDTO.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "LoginRequestDTO.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "RegisterRequest.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "RegisterRequestDTO.java"));
        Files.deleteIfExists(Paths.get(dtoDir, "UserDTO.java"));
        
        // Try to delete security directory if empty
        try {
            Files.delete(Paths.get(baseDir, "security"));
        } catch (DirectoryNotEmptyException e) {
            System.out.println("Security directory not empty");
        }
    }
}
