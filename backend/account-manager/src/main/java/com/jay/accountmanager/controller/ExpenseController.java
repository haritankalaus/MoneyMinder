package com.jay.accountmanager.controller;

import com.jay.accountmanager.model.Expense;
import com.jay.accountmanager.model.ExpenseType;
import com.jay.accountmanager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/expenses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/expenses/types")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExpenseType>> getAllExpenseTypes() {
        return ResponseEntity.ok(expenseService.getAllExpenseTypes());
    }

    @GetMapping("/expense-types")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExpenseType>> getAllExpenseTypesOld() {
        return ResponseEntity.ok(expenseService.getAllExpenseTypes());
    }

    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @PutMapping("/expenses/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expense));
    }

    @DeleteMapping("/expenses/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expenses/type/{category}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Expense>> getExpensesByType(@PathVariable String category) {
        return ResponseEntity.ok(expenseService.getExpensesByType(category));
    }
}
