package com.example.BankSystem.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UpdateCustomerRequest {
    @Length(max = 50)
    private String firstName;
    @Length(max = 50)
    private String lastName;
    @Length(max = 50)
    private String middleName;
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    @Length(max = 200)
    private String address;
}
