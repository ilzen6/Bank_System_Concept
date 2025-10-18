package com.example.BankSystem.service;

import com.example.BankSystem.dto.MapStructs.CardMapper;
import com.example.BankSystem.dto.request.CreateCardRequest;
import com.example.BankSystem.dto.response.CardResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Card;
import com.example.BankSystem.entity.Customer;
import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.exception.AccountNotFoundException;
import com.example.BankSystem.exception.CardNotFoundException;
import com.example.BankSystem.exception.CustomerNotFoundException;
import com.example.BankSystem.exception.InvalidOperationException;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.repository.CardRepository;
import com.example.BankSystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponse issueCard(CreateCardRequest request) {
        // Найти клиента
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        // Найти счет
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + request.getAccountId()));

        // Проверить что счет принадлежит клиенту
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new InvalidOperationException(
                    "Account does not belong to customer. Account ID: " +
                            request.getAccountId() + ", Customer ID: " + request.getCustomerId());
        }

        // Проверить что счет активен
        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidOperationException(
                    "Cannot issue card for inactive account. Account status: " +
                            account.getStatus());
        }

        // Создать карту
        Card card = cardMapper.toEntity(request);

        // Установить связи
        card.setCustomer(customer);
        card.setAccount(account);
        card.setHolderName(customer.getFullName());

        // Сгенерировать CVV (3 цифры)
        Random random = new Random();
        int cvvNumber = random.nextInt(900) + 100; // от 100 до 999
        card.setCvv(String.valueOf(cvvNumber));

        // Сгенерировать PIN (4 цифры)
        int pinNumber = random.nextInt(9000) + 1000; // от 1000 до 9999
        card.setPinCode(String.valueOf(pinNumber));

        // Добавить через helper методы
        customer.addCard(card);
        account.addCard(card);

        // Сохранить
        Card savedCard = cardRepository.save(card);

        log.info("Card issued: {} for account {}",
                savedCard.getMaskCardNumber(), account.getAccountNumber());

        return cardMapper.toResponse(savedCard);
    }

    public CardResponse getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + id));
        return cardMapper.toResponse(card);
    }

    public CardResponse getCardByNumber(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with number: " + cardNumber));
        return cardMapper.toResponse(card);
    }

    public List<CardResponse> getCustomerCards(Long customerId) {
        // Проверить существование клиента
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));

        List<Card> cards = cardRepository.findAllByCustomerId(customerId);
        return cardMapper.toResponseList(cards);
    }

    public List<CardResponse> getAccountCards(Long accountId) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        List<Card> cards = cardRepository.findAllByAccountId(accountId);
        return cardMapper.toResponseList(cards);
    }

    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId));

        card.setIsBlocked(true);
        cardRepository.save(card);

        log.info("Card blocked: {}", card.getMaskCardNumber());
    }

    @Transactional
    public void unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId));

        card.setIsBlocked(false);
        cardRepository.save(card);

        log.info("Card unblocked: {}", card.getMaskCardNumber());
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId));

        cardRepository.delete(card);

        log.info("Card deleted: ID={}, Number={}", cardId, card.getMaskCardNumber());
    }
}