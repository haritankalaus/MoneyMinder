package com.jay.accountmanager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.DashboardResponseDTO;
import com.jay.accountmanager.dto.PaymentDTO;
import com.jay.accountmanager.dto.TransactionDTO;
import com.jay.accountmanager.service.DashboardService;
import com.jay.security.models.User;
import com.jay.security.services.AuthenticationFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Dashboard", description = "Dashboard management APIs")
@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final AuthenticationFacade authenticationFacade;

    @Operation(summary = "Get dashboard data", description = "Retrieves all dashboard data including stats, transactions, and payments")
    @GetMapping
    public DashboardResponseDTO getDashboardData() {
        User user = authenticationFacade.getCurrentUser();
        return dashboardService.getDashboardData(user);
    }

    @Operation(summary = "Get transactions", description = "Retrieves paginated list of transactions")
    @GetMapping("/transactions")
    public Page<TransactionDTO> getTransactions(Pageable pageable) {
        User user = authenticationFacade.getCurrentUser();
        return dashboardService.getTransactions(user, pageable);
    }

    @Operation(summary = "Get payments", description = "Retrieves paginated list of payments")
    @GetMapping("/payments")
    public Page<PaymentDTO> getPayments(Pageable pageable) {
        User user = authenticationFacade.getCurrentUser();
        return dashboardService.getPayments(user, pageable);
    }
}
