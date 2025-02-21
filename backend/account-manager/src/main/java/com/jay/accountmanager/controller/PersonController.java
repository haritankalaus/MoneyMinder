package com.jay.accountmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.CreatePersonRequest;
import com.jay.accountmanager.dto.PersonDTO;
import com.jay.accountmanager.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Person Management", description = "Endpoints for managing person profile and preferences")
public class PersonController {

    private final PersonService personService;

    @GetMapping("/profile")
    @Operation(summary = "Get current person's profile")
    public ResponseEntity<PersonDTO> getCurrentPersonProfile() {
        return ResponseEntity.ok(personService.getCurrentPersonProfile());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current person's profile")
    public ResponseEntity<PersonDTO> updateProfile(@Valid @RequestBody PersonDTO personDTO) {
        return ResponseEntity.ok(personService.updateProfile(personDTO));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update current person's preferences")
    public ResponseEntity<PersonDTO> updatePreferences(@Valid @RequestBody PersonDTO preferencesDTO) {
        return ResponseEntity.ok(personService.updatePreferences(preferencesDTO));
    }

    @PostMapping("/create")
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody CreatePersonRequest request) {
        PersonDTO personDTO = personService.createPerson(request);
        return ResponseEntity.ok(personDTO);
    }
    
}
