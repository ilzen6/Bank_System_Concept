package com.example.BankSystem.repository;


import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByCustomer(Customer customer);

    List<Account> findAllByCustomerId(Long customerId);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findAllByAccountType(AccountType type);

    List<Account> findAllByStatus(AccountStatus status);

    List<Account> findAllByAccountTypeAndStatus(AccountType type, AccountStatus status);

    List<Account> findAllByBalanceGreaterThan(BigDecimal balance);

    List<Account> findAllByBalanceBetween(BigDecimal min, BigDecimal max);

    List<Account> findAllByCurrency(String currency);

    List<Account> findAllByCurrencyAndStatus(String currency, AccountStatus status);

    List<Account> findAllByCustomerIdAndStatus(Long customerId, AccountStatus status);

    long countByCustomerId(Long customerId);
    long countByStatus(AccountStatus status);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.customer.id = :customerId AND a.status = :status")
    BigDecimal getTotalBalanceByCustomerAndStatus(@Param("customerId") Long customerId,
                                                  @Param("status") AccountStatus status);


}
