package com.example.BankSystem.controller;

import com.example.BankSystem.dto.request.CreateCustomerRequest;
import com.example.BankSystem.dto.request.UpdateCustomerRequest;
import com.example.BankSystem.dto.response.AccountResponse;
import com.example.BankSystem.dto.response.CardResponse;
import com.example.BankSystem.dto.response.CustomerDetailResponse;
import com.example.BankSystem.dto.response.CustomerResponse;
import com.example.BankSystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDetailResponse> registerCustomer(@RequestBody @Valid CreateCustomerRequest request) {
        CustomerDetailResponse response = customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailResponse> getCustomerById(@PathVariable Long id) {
        CustomerDetailResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDetailResponse> getCustomerByEmail(@PathVariable String email) {
        CustomerDetailResponse response = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> getByLastName(@RequestParam String lastName) {
        List<CustomerResponse> responses = customerService.searchCustomersByLastName(lastName);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDetailResponse> updateCustomer(@PathVariable Long id,
                                                                 @RequestBody @Valid UpdateCustomerRequest request) {
        CustomerDetailResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@PathVariable Long id) {
        List<AccountResponse> accounts = customerService.getCustomerAccounts(id);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardResponse>> getCustomerCards(@PathVariable Long id) {
        List<CardResponse> cards = customerService.getCustomerCards(id);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerTotalBalance(id));
    }

}
