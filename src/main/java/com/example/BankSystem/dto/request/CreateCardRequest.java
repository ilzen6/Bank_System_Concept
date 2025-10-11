package com.example.BankSystem.dto.request;

import com.example.BankSystem.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCardRequest {

    @NotBlank
    private Long customerId;

    @NotBlank
    private Long accountId;

    @NotBlank
    private CardType cardType;
    private BigDecimal dailyLimit = new BigDecimal("100000.00");
}
