package com.jay.accountmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jay.accountmanager.dto.BillDTO;
import com.jay.accountmanager.dto.BillStatsDTO;
import com.jay.accountmanager.service.BillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBill(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBill(id));
    }

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@RequestBody BillDTO bill) {
        return ResponseEntity.ok(billService.createBill(bill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable Long id, @RequestBody BillDTO bill) {
        return ResponseEntity.ok(billService.updateBill(id, bill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<BillStatsDTO> getBillStats() {
        return ResponseEntity.ok(billService.getBillStats());
    }
}
