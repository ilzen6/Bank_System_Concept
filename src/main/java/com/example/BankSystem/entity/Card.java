package com.example.BankSystem.entity;

import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.AccountType;
import com.example.BankSystem.enums.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cards", indexes = {
        @Index(name = "uq_card_number", columnList = "cardNumber", unique = true),
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_type_blocked", columnList = "cardType,isBlocked")
})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Length(min = 16, max = 16)
    private String cardNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    @Column(nullable = false, length = 100)
    private String holderName;
    @Column(nullable = false)
    @Length(min = 3, max = 3)
    private String cvv;
    @Column(nullable = false)
    @Length(min = 4, max = 4)
    private String pinCode;
    @Column(nullable = false)
    private BigDecimal dailyLimit = new BigDecimal("100000.00");
    @Column(nullable = false)
    private Boolean isBlocked = false;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;



    @Transient
    private Boolean isExpired;
    @Transient
    private Boolean isValid;
    @Transient
    private String maskCardNumber = "";



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @PrePersist
    public void beforeSave() {
        if (cardNumber == null) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            this.cardNumber = sb.toString();
        }
        if (expiryDate == null) {
            this.expiryDate = issueDate.plusYears(4);
        }
    }
    @PostLoad
    public void calculateArgs() {
        this.isExpired = this.expiryDate.isBefore(LocalDate.now());
        this.isValid = !isExpired && !isBlocked;

        if (cardNumber != null && cardNumber.length() == 16) {
            this.maskCardNumber = "**** **** **** " + cardNumber.substring(12);
        }
    }

    @Override
    public String toString() {
        return "Card{id=" + id + ",cardNumber='" + maskCardNumber + "',cardType='" +
                cardType + "',holderName='" + holderName + "',dailyLimit=" + dailyLimit +
                ",isBlocked='" + isBlocked + "',issueDate='" + issueDate + "',expiryDate='" +
                expiryDate + "',isExpired='" + isExpired + "',isValid='" + isValid +
                "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Card card = (Card) obj;
        return Objects.equals(this.cardNumber, card.getCardNumber());
    }


    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

}
