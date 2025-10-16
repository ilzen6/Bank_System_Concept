package com.example.BankSystem.repository;

import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Transaction;
import com.example.BankSystem.enums.TransactionStatus;
import com.example.BankSystem.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findAllByFromAccount(Account fromAccount);
    List<Transaction> findAllByToAccount(Account toAccount);
    List<Transaction> findAllByFromAccountOrToAccount(Account fromAccount, Account toAccount);
    List<Transaction> findAllByFromAccountId(Long accountId);
    List<Transaction> findAllByToAccountId(Long accountId);
    List<Transaction> findAllByTransactionType(TransactionType transactionType);
    List<Transaction> findAllByStatus(TransactionStatus status);
    List<Transaction> findAllByTransactionTypeAndStatus(TransactionType type, TransactionStatus status);
    List<Transaction> findAllByTransactionDateAfter(LocalDateTime date);
    List<Transaction> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    List<Transaction> findAllByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(Long fromId, Long toId);
    List<Transaction> findAllByAmountGreaterThan(BigDecimal amount);
    List<Transaction> findAllByAmountBetween(BigDecimal min, BigDecimal max);
    long countByFromAccountId(Long accountId);
    long countByStatus(TransactionStatus status);
    List<Transaction> findTop10ByOrderByAmountDesc();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.status = :status")
    BigDecimal getTotalAmountByAccountAndStatus(@Param("accountId") Long accountId,
                                                @Param("status") TransactionStatus status);


}
