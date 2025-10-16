package com.example.BankSystem.service;

import com.example.BankSystem.dto.MapStructs.AccountMapper;
import com.example.BankSystem.dto.MapStructs.CardMapper;
import com.example.BankSystem.dto.MapStructs.CustomerMapper;
import com.example.BankSystem.dto.request.CreateCustomerRequest;
import com.example.BankSystem.dto.request.UpdateCustomerRequest;
import com.example.BankSystem.dto.response.AccountResponse;
import com.example.BankSystem.dto.response.CardResponse;
import com.example.BankSystem.dto.response.CustomerDetailResponse;
import com.example.BankSystem.dto.response.CustomerResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Card;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.exception.CustomerNotFoundException;
import com.example.BankSystem.exception.InvalidOperationException;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.repository.CardRepository;
import com.example.BankSystem.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class CustomerService {
    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;
    private CardRepository cardRepository;
    private CustomerMapper customerMapper;
    private AccountMapper accountMapper;
    private CardMapper cardMapper;

    public CustomerService(CustomerRepository customerRepository,
                           AccountRepository accountRepository,
                           CardRepository cardRepository,
                           CustomerMapper customerMapper,
                           AccountMapper accountMapper, CardMapper cardMapper) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.customerMapper = customerMapper;
        this.accountMapper = accountMapper;
        this.cardMapper = cardMapper;
    }

    @Transactional
    public CustomerDetailResponse registerCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Пользователь с таким email уже существует");
        }

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new InvalidOperationException("Пользователь с таким номером телефона уже существует");
        }
        if (customerRepository.existsByPassportNumber(request.getPassportNumber())) {
            throw new InvalidOperationException("Пользователь с такими паспортными данными уже существует");
        }
        Customer customer = customerMapper.toEntity(request);
        customerRepository.save(customer);
        log.info("New customer registered: ID = {}, Email = {}", customer.getId(), customer.getEmail());
        return customerMapper.toDetailResponse(customer);
    }

    public CustomerDetailResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDetailResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        log.info("Retrieved {} customers", customers.size());
        return customerMapper.toResponseList(customers);
    }

    @Transactional
    public CustomerDetailResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + id));
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new InvalidOperationException("Пользователь с таким номером телефона уже существует");
        }
        customerMapper.updateEntityFromRequest(request, customer);
        customerRepository.save(customer);
        log.info("Customer updated: ID={}", id);
        return customerMapper.toDetailResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + id)
        );
        List<Account> activeAccounts = accountRepository.findAllByStatus(AccountStatus.ACTIVE);
        if (!(activeAccounts.isEmpty())) {
            throw new InvalidOperationException("Cannot delete customer" +
                    " with active accounts. Customer ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted:ID={}", id);
    }

    public List<CustomerResponse> searchCustomersByLastName(String lastName) {
        List<Customer> customers = customerRepository.findAllByLastNameContainingIgnoreCase(lastName);
        return customerMapper.toResponseList(customers);
    }
    public CustomerDetailResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with email: " + email)
        );
        return customerMapper.toDetailResponse(customer);
    }
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + customerId)
        );
        List<Account> accounts = accountRepository.findAllByCustomerId(customerId);

        return accountMapper.toResponseList(accounts);
    }
    public List<CardResponse> getCustomerCards(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + customerId)
        );
        List<Card> cards = cardRepository.findAllByCustomerId(customerId);
        return cardMapper.toResponseList(cards);
    }
    public BigDecimal getCustomerTotalBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + customerId)
        );
        BigDecimal balance = accountRepository.getTotalBalanceByCustomerAndStatus(
                customerId, AccountStatus.ACTIVE);
        if (balance == null) {
            return BigDecimal.ZERO;
        }
        return balance;
    }
}
