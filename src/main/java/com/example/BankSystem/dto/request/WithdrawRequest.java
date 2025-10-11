package com.example.BankSystem.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WithdrawRequest {
    @NotNull

    private Long accountId;
    @NotNull

    @DecimalMin("0.01")
    private BigDecimal amount;
    @Length(max = 500)
    private String description;
}
