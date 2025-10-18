package com.example.BankSystem.controller;

import com.example.BankSystem.dto.request.TransferRequest;
import com.example.BankSystem.dto.response.TransactionResponse;
import com.example.BankSystem.dto.response.TransferResponse;
import com.example.BankSystem.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request) {
        TransferResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction-id/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionByTransactionId(
            @PathVariable String transactionId) {
        TransactionResponse response = transactionService
                .getTransactionByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService
                .getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<TransactionResponse> transactions = transactionService
                .getTransactionHistory(accountId, from, to);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/successful")
    public ResponseEntity<List<TransactionResponse>> getSuccessfulTransactions(
            @PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService
                .getSuccessfulTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/total-incoming")
    public ResponseEntity<BigDecimal> calculateTotalIncoming(@PathVariable Long accountId) {
        BigDecimal total = transactionService.calculateTotalIncoming(accountId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/account/{accountId}/total-outgoing")
    public ResponseEntity<BigDecimal> calculateTotalOutgoing(@PathVariable Long accountId) {
        BigDecimal total = transactionService.calculateTotalOutgoing(accountId);
        return ResponseEntity.ok(total);
    }
}