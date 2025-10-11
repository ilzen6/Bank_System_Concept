package com.example.BankSystem.entity;


import com.example.BankSystem.enums.TransactionStatus;
import com.example.BankSystem.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Slf4j
@Data
@Table(name = "transactions", indexes = {
        @Index(name = "uq_transaction_id", columnList = "transactionId", unique = true),
        @Index(name = "idx_from_account", columnList = "fromAccount_id"),
        @Index(name = "idx_to_account", columnList = "toAccount_id"),
        @Index(name = "idx_transaction_date", columnList = "transactionDate"),
        @Index(name = "idx_status_type", columnList = "status,transactionType")
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 36)
    private String transactionId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    @Column(nullable = false)
    @Length(min = 3, max = 3)
    private String currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Transient
    private Boolean isSuccessful;
    @Transient
    private String displayInfo;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @PostLoad
    public void calculateArgs() {
        if (status.equals(TransactionStatus.SUCCESS)) {
            this.isSuccessful = true;
        }
        displayInfo = "[" + transactionType + "][" + amount + "][" +
                currency + "]-[" + status +"]";

    }
    @PrePersist
    public void beforeSave() {
        if (transactionId == null) {
            this.transactionId = UUID.randomUUID().toString();
        }
        if (transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }

    }

    @PrePersist
    @PreUpdate
    public void checkAmount() {
        if (this.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException();
        }
    }
    @PostPersist
    public void createTransactionLog() {
        log.info("Создана транзакция: [{}], Сумма: [{}] [{}]", id, amount, currency);
    }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ",transactionId=" + transactionId
                + ",transactionType='" + transactionType + "',amount=" + amount + ",currency='" +
                currency + "',status='" + status + "',description='" + description +
                "',transactionDate='" + transactionDate + "',isSuccessful='" + isSuccessful +
                "',displayInfo='" + displayInfo + "'}";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) obj;
        return Objects.equals(transaction.getTransactionId(), this.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId);
    }
}
