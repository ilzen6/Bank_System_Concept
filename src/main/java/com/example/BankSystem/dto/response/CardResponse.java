package com.example.BankSystem.dto.response;


import com.example.BankSystem.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardResponse {
    private Long id;
    private String maskedCardNumber;
    private CardType cardType;
    private String holderName;
    private LocalDate expiryDate;
    private BigDecimal dailyLimit;
    private Boolean isBlocked;
    private Boolean isExpired;
    private Boolean isValid;
    private LocalDate issueDate;
    private String accountNumber;
}
