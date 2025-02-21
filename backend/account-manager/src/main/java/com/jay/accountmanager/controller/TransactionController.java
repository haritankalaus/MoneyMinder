package com.jay.accountmanager.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.TransactionDTO;
import com.jay.accountmanager.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getAccountTransactions(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(transactionService.getAccountTransactions(accountId));
    }

    @GetMapping("/account/{accountId}/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(accountId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Valid @RequestBody TransactionDTO transactionDTO
    ) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
    }
}
