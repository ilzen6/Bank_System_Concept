package com.example.BankSystem.dto.request;

import com.example.BankSystem.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateAccountRequest {
    @NotBlank
    private Long customerId;
    @NotBlank
    private AccountType accountType;
    @Length(min = 3, max = 3)
    private String currency = "RUB";
    private Double interestRate;
}
