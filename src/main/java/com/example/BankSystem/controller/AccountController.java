package com.example.BankSystem.controller;


import com.example.BankSystem.dto.request.CreateAccountRequest;
import com.example.BankSystem.dto.request.DepositRequest;
import com.example.BankSystem.dto.request.WithdrawRequest;
import com.example.BankSystem.dto.response.AccountDetailResponse;
import com.example.BankSystem.dto.response.AccountResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDetailResponse> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        AccountDetailResponse response = accountService.openAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> responses = accountService.getAllAccounts();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailResponse> getAccountById(@PathVariable Long id) {
        AccountDetailResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search/{accountNumber}")
    public ResponseEntity<AccountDetailResponse> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDetailResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable Long customerId) {
        List<AccountResponse> responses = accountService.getCustomerAccounts(customerId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountDetailResponse> deposit(@PathVariable Long id,
                                                         @RequestBody @Valid DepositRequest request) {
        AccountDetailResponse response = accountService.deposit(id, request.getAmount(), request.getDescription());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountDetailResponse> withdraw(@PathVariable Long id,
                                                          @RequestBody @Valid WithdrawRequest request) {
        AccountDetailResponse response = accountService.withdraw(id, request.getAmount(),
                request.getDescription());
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable Long id) {
        accountService.closeAccount(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        accountService.blockedAccount(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable Long id) {
        accountService.unblockAccount(id);
        return ResponseEntity.noContent().build();
    }
}
