package com.example.BankSystem.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Entity
@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers", indexes = {
        @Index(name = "uq_email", columnList = "email", unique = true),
        @Index(name = "uq_phone", columnList = "phoneNumber", unique = true),
        @Index(name = "uq_passport", columnList = "passportNumber", unique = true),
        @Index(name = "idx_last_name", columnList = "lastName")
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(length = 50)
    private String middleName;
    @Column(nullable = false, unique = true, length = 100)
    @Email
    private String email;
    @Column(nullable = false, unique = true, length = 15)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    @Column(nullable = false, unique = true)
    @Length(min = 10, max = 10)
    private String passportNumber;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @Column(nullable = false, length = 200)
    private String address;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Transient
    private String fullName;
    @Transient
    private Integer age;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,
    fetch = FetchType.LAZY)
    private List<Account> accounts;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,
    fetch = FetchType.LAZY)
    private List<Card> cards;

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setCustomer(this);
    }
    public void removeAccount(Account account) {
        this.accounts.remove(account);
        account.setCustomer(null);
    }
    public void addCard(Card card) {
        this.cards.add(card);
        card.setCustomer(this);
    }
    public void removeCard(Card card) {
        this.cards.remove(card);
        card.setCustomer(null);
    }


    @PostLoad
    public void calculateTransArg() {
        if (middleName != null) {
            this.fullName = firstName + " " + middleName + " " + lastName;
        } else this.fullName = firstName + " " + lastName;

        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @PostPersist
    public void addLogCreate() {
        log.info("Зарегистрирован новый клиент: [{}], Email: [{}] ", fullName, email);
    }

    @Override
    public String toString() {
        return "Customer {id=" + id + ", fullName='" + fullName + "', email='" + email +
                "', phoneNumber=" + phoneNumber + ", passportNumber=" + passportNumber +
                ", age=" + age + ", address='" + address + "', registrationDate='" +
                registrationDate + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Customer customer = (Customer) obj;
        return Objects.equals(this.passportNumber, customer.getPassportNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.passportNumber);
    }


}
