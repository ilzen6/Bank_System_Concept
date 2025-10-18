package com.example.BankSystem.service;

import com.example.BankSystem.dto.MapStructs.AccountMapper;
import com.example.BankSystem.dto.MapStructs.TransactionMapper;
import com.example.BankSystem.dto.request.CreateAccountRequest;
import com.example.BankSystem.dto.request.DepositRequest;
import com.example.BankSystem.dto.response.AccountDetailResponse;
import com.example.BankSystem.dto.response.AccountResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.entity.Transaction;
import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.TransactionStatus;
import com.example.BankSystem.enums.TransactionType;
import com.example.BankSystem.exception.*;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.repository.CustomerRepository;
import com.example.BankSystem.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;


    @Transactional
    public AccountDetailResponse openAccount(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new CustomerNotFoundException("Customer nor found with id: " +
                        request.getCustomerId())
        );
        Account account = accountMapper.toEntity(request);
        customer.addAccount(account);
        accountRepository.save(account);
        log.info("New account opened: {} for customer ID={}", account.getAccountNumber(),
                customer.getId());
        return accountMapper.toDetailResponse(account);
    }

    public AccountDetailResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id = " + id)
        );

        return accountMapper.toDetailResponse(account);
    }

    public AccountDetailResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException("Account not found with account number = "
                        + accountNumber)
        );

        return accountMapper.toDetailResponse(account);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountMapper.toResponseList(accountRepository.findAll());
    }

    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id =" + customerId)
        );
        return accountMapper.toResponseList(accountRepository.findAllByCustomerId(customerId));
    }

    @Transactional
    public AccountDetailResponse deposit(Long accountId, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id = " + accountId)
        );

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransactionException("Amount must be positive: " + amount);
        }

        if (!(account.getStatus().equals(AccountStatus.ACTIVE))) {
            throw new InvalidOperationException("Account is not active. Status: "
                    + account.getStatus());
        }

        account.setBalance(account.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description);
        transaction.setToAccount(account);
        transaction.setFromAccount(null);

        transactionRepository.save(transaction);
        accountRepository.save(account);
        log.info("Deposit completed: {} {} to account {}. New balance: {}",
                amount, account.getCurrency(), account.getAccountNumber(), account.getBalance());
        return accountMapper.toDetailResponse(account);
    }

    @Transactional
    public AccountDetailResponse withdraw(Long accountId, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId)
        );
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOperationException("Amount must be positive");
        }
        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidOperationException("Account not active");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        account.setBalance(account.getBalance().subtract(amount));
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description);
        transaction.setFromAccount(account);
        transaction.setToAccount(null);

        transactionRepository.save(transaction);
        accountRepository.save(account);
        log.info("Withdraw completed: {} {} from account: {}. New balance: {}",
                amount, account.getCurrency(), account.getAccountNumber(), account.getBalance());
        return accountMapper.toDetailResponse(account);
    }

    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId)
        );
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidOperationException("Account balance not empty");
        }
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        log.info("Account {} was closed", account.getAccountNumber());
    }

    @Transactional
    public void blockedAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId)
        );
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.info("Account {} was blocked", account.getAccountNumber());
    }

    @Transactional
    public void unblockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId)
        );
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Account {} was unblocked", account.getAccountNumber());
    }


}
