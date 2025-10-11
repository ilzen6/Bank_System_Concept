package com.example.BankSystem.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerDetailResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String passportNumber;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private LocalDate registrationDate;
    private Integer accountsCount;
    private Integer cardsCount;
}
