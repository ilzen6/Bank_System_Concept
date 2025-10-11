package com.example.BankSystem.dto.response;

import com.example.BankSystem.enums.TransactionStatus;
import com.example.BankSystem.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private String description;
    private LocalDateTime transactionDate;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Boolean isSuccessful;



}
