package com.example.BankSystem.entity;

import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts", indexes =  {
        @Index(name = "uq_account_number", columnList = "accountNumber", unique = true),
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_type_status", columnList = "accountType,status")
})

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @Length(min = 20, max = 20)
    private String accountNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Column(nullable = false)
    @Min(0)
    private BigDecimal balance = new BigDecimal("0.00");
    @Column(nullable = false)
    @Length(min = 3, max = 3)
    private String currency = "RUB";
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Min(0)
    @Max(20)
    private Double interestRate;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openDate;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Transient
    private boolean isActive;
    @Transient
    private String displayInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<Card> cards;

    public void addCard(Card card) {
        this.cards.add(card);
        card.setAccount(this);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
        card.setAccount(null);
    }

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL,
    fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> outgoingTransactions;

    public void addOutgoingTransaction(Transaction transaction) {
        this.outgoingTransactions.add(transaction);
        transaction.setToAccount(this);
    }
    public void removeOutgoingTransaction(Transaction transaction)  {
        this.outgoingTransactions.remove(transaction);
        transaction.setToAccount(null);
    }

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
    orphanRemoval = true)
    private List<Transaction> incomingTransactions;

    public void addIncomingTransaction(Transaction transaction) {
        this.incomingTransactions.add(transaction);
        transaction.setFromAccount(this);
    }
    public void removeIncomingTransaction(Transaction transaction) {
        this.incomingTransactions.remove(transaction);
        transaction.setFromAccount(null);
    }




    @PostLoad
    public void calculateArgs() {
        this.isActive = (this.status.equals(AccountStatus.ACTIVE));
        this.displayInfo = "Счет: [" + accountNumber + "]-[" + accountType.name() + "] ([" +
        balance + "][" + currency + "])";
    }

    @PrePersist
    @PreUpdate
    public void checkBalance() {
        if (this.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException();
        }
    }
    @PrePersist
    public void checkAccountNumber() {
        if (this.accountNumber == null) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder("40817810");
            for (int i = 0; i < 12; i++) {  // еще 12 цифр
                sb.append(random.nextInt(10));  // цифра от 0 до 9
            }
            this.accountNumber = sb.toString();
        }
    }
    @PostPersist
    public void createNewAccountLog() {
        log.info("Открыт новый счет: [{}] для клиента [{}]", accountNumber, customer.getFullName());
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ",accountNumber='" + accountNumber +
                "',accountType='" + accountType.name() +
                "',balance=" + balance + ",status='" + status.name() +
                "',interestName='" + interestRate + "',openDate='" + openDate +
                "',isActive='" + isActive + "',displayInfo='" + displayInfo + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Account account = (Account) obj;
        return Objects.equals(account.getAccountNumber(), this.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
