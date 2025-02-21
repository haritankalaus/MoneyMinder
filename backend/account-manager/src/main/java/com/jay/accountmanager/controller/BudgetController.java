package com.jay.accountmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.BudgetDTO;
import com.jay.accountmanager.dto.BudgetSummaryDTO;
import com.jay.accountmanager.service.BudgetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget Management", description = "APIs for managing budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(
        summary = "Create a new budget",
        description = "Creates a new budget with specified categories and allocations"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Budget created successfully",
            content = @Content(schema = @Schema(implementation = BudgetDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid budget data provided"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Budget already exists for the specified period"
        )
    })
    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(
        @Valid @RequestBody BudgetDTO budgetDTO
    ) {
        return ResponseEntity.ok(budgetService.createBudget(budgetDTO));
    }

    @Operation(
        summary = "Get current budget",
        description = "Retrieves the budget for the current period"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Current budget retrieved successfully",
            content = @Content(schema = @Schema(implementation = BudgetDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No budget found for current period"
        )
    })
    @GetMapping("/current")
    public ResponseEntity<BudgetDTO> getCurrentBudget() {
        return ResponseEntity.ok(budgetService.getCurrentBudget());
    }

    @Operation(
        summary = "Get budget summary",
        description = "Retrieves a summary of the specified budget including spending analysis"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Budget summary retrieved successfully",
            content = @Content(schema = @Schema(implementation = BudgetSummaryDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Budget not found"
        )
    })
    @GetMapping("/{id}/summary")
    public ResponseEntity<BudgetSummaryDTO> getBudgetSummary(
        @Parameter(description = "Budget ID", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(budgetService.getBudgetSummary(id));
    }
}
