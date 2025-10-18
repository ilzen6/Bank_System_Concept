package com.example.BankSystem.repository;

import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Card;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findAllByAccount(Account account);

    List<Card> findAllByAccountId(Long accountId);

    List<Card> findAllByCustomer(Customer customer);

    List<Card> findAllByCustomerId(Long customerId);

    boolean existsByCardNumber(String cardNumber);

    List<Card> findAllByCardType(CardType type);

    List<Card> findAllByCustomerIdAndCardType(Long customerId, CardType cardType);

    List<Card> findAllByIsBlockedTrue();

    List<Card> findAllByIsBlockedFalse();

    List<Card> findAllByCustomerIdAndIsBlockedFalse(Long customerId);

    List<Card> findAllByExpiryDateBefore(LocalDate date);

    long countByCustomerId(Long customerId);

    long countByIsBlockedTrue();




}
