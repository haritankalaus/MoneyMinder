package com.jay.accountmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String language;
    private String currency;
    private boolean notifications;
}
