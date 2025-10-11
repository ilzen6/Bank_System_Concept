package com.example.BankSystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCustomerRequest {
    @Length(max = 50)
    @NotBlank
    private String firstName;
    @Length(max = 50)
    @NotNull
    @NotBlank
    private String lastName;
    @Length(max = 50)
    private String middleName;
    @NotNull
    @Email
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    @NotNull
    @NotBlank
    @Length(min = 10, max = 10)
    private String passportNumber;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @NotNull
    @NotBlank
    @Length(max = 200)
    private String address;

}
