package com.jay.accountmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.IncomeRequest;
import com.jay.accountmanager.dto.IncomeStatsDTO;
import com.jay.accountmanager.model.Income;
import com.jay.accountmanager.service.IncomeService;
import com.jay.security.services.AuthenticationFacade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;
    
    private final AuthenticationFacade authenticationFacade;
    
    @PostMapping
    public ResponseEntity<Income> createIncome(
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.createIncome(authenticationFacade.getLoggedinUserId(), request));
    }

    @GetMapping
    public ResponseEntity<List<Income>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes(authenticationFacade.getLoggedinUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Income> getIncome(
            @PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getIncome(authenticationFacade.getLoggedinUserId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.updateIncome(authenticationFacade.getLoggedinUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            Authentication authentication,
            @PathVariable Long id) {
        incomeService.deleteIncome(authenticationFacade.getLoggedinUserId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<IncomeStatsDTO> getIncomeStats() {
        return ResponseEntity.ok(incomeService.getIncomeStats(authenticationFacade.getLoggedinUserId()));
    }
}
