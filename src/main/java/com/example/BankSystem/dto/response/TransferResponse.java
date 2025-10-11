package com.example.BankSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferResponse {
    private Boolean success;
    private String message;
    private TransactionResponse transaction;
    private BigDecimal fromAccountBalance;
    private BigDecimal toAccountBalance;
}
