package com.example.BankSystem.controller;

import com.example.BankSystem.dto.request.CreateCardRequest;
import com.example.BankSystem.dto.response.CardResponse;
import com.example.BankSystem.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> issueCard(@RequestBody @Valid CreateCardRequest request) {
        CardResponse response = cardService.issueCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        CardResponse response = cardService.getCardById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CardResponse> getCardByNumber(@PathVariable String cardNumber) {
        CardResponse response = cardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CardResponse>> getCustomerCards(@PathVariable Long customerId) {
        List<CardResponse> cards = cardService.getCustomerCards(customerId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponse>> getAccountCards(@PathVariable Long accountId) {
        List<CardResponse> cards = cardService.getAccountCards(accountId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long id) {
        cardService.blockCard(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockCard(@PathVariable Long id) {
        cardService.unblockCard(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}