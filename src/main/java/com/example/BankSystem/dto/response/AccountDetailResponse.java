package com.example.BankSystem.dto.response;


import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class AccountDetailResponse {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private Double interestRate;
    private LocalDate openDate;
    private Boolean isActive;
    private String displayInfo;
    private String customerFullName;
    private Integer cardsCount;
}
