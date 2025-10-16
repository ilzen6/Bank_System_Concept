package com.example.BankSystem.service;

import com.example.BankSystem.dto.MapStructs.AccountMapper;
import com.example.BankSystem.dto.MapStructs.TransactionMapper;
import com.example.BankSystem.dto.request.CreateAccountRequest;
import com.example.BankSystem.dto.response.AccountDetailResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.exception.CustomerNotFoundException;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.repository.CustomerRepository;
import com.example.BankSystem.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private TransactionRepository transactionRepository;
    private AccountMapper accountMapper;
    private TransactionMapper transactionMapper;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionRepository transactionRepository,
                          AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

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

}
