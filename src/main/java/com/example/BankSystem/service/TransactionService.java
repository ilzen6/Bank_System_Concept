package com.example.BankSystem.service;

import com.example.BankSystem.dto.MapStructs.TransactionMapper;
import com.example.BankSystem.dto.request.TransferRequest;
import com.example.BankSystem.dto.response.TransactionResponse;
import com.example.BankSystem.dto.response.TransferResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Transaction;
import com.example.BankSystem.enums.AccountStatus;
import com.example.BankSystem.enums.TransactionStatus;
import com.example.BankSystem.enums.TransactionType;
import com.example.BankSystem.exception.AccountNotFoundException;
import com.example.BankSystem.exception.InsufficientFundsException;
import com.example.BankSystem.exception.InvalidTransactionException;
import com.example.BankSystem.exception.TransactionNotFoundException;
import com.example.BankSystem.repository.AccountRepository;
import com.example.BankSystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        // Найти оба счета
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "From account not found with id: " + request.getFromAccountId()));

        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "To account not found with id: " + request.getToAccountId()));

        // Проверить что оба счета активны
        if (!fromAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidTransactionException(
                    "From account is not active. Status: " + fromAccount.getStatus());
        }

        if (!toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidTransactionException(
                    "To account is not active. Status: " + toAccount.getStatus());
        }

        // Проверить сумму
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                    "Amount must be positive: " + amount);
        }

        // Проверить достаточно ли средств
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available: " + fromAccount.getBalance() +
                            " " + fromAccount.getCurrency() +
                            ", Requested: " + amount + " " + fromAccount.getCurrency());
        }

        // Проверить что валюты совпадают
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new InvalidTransactionException(
                    "Currency mismatch. From account: " + fromAccount.getCurrency() +
                            ", To account: " + toAccount.getCurrency());
        }

        // Выполнить перевод
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Создать транзакцию
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(request.getDescription());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);

        // Сохранить всё
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transfer completed: {} {} from account {} to account {}. Transaction ID: {}",
                amount, fromAccount.getCurrency(),
                fromAccount.getAccountNumber(), toAccount.getAccountNumber(),
                savedTransaction.getTransactionId());

        // Создать response
        return transactionMapper.toTransferResponse(savedTransaction, fromAccount, toAccount);
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with id: " + id));
        return transactionMapper.toResponse(transaction);
    }

    public TransactionResponse getTransactionByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with transaction ID: " + transactionId));
        return transactionMapper.toResponse(transaction);
    }

    public List<TransactionResponse> getAccountTransactions(Long accountId) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        // Получить все транзакции (входящие и исходящие), отсортированные по дате
        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(accountId, accountId);

        return transactionMapper.toResponseList(transactions);
    }

    public List<TransactionResponse> getTransactionHistory(Long accountId,
                                                           LocalDateTime from,
                                                           LocalDateTime to) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        // Получить транзакции в диапазоне дат
        List<Transaction> allTransactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(accountId, accountId);

        // Фильтровать по датам
        List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(from) &&
                        !t.getTransactionDate().isAfter(to))
                .toList();

        return transactionMapper.toResponseList(filteredTransactions);
    }

    public List<TransactionResponse> getSuccessfulTransactions(Long accountId) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        // Получить все транзакции счета
        List<Transaction> allTransactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(accountId, accountId);

        // Фильтровать только успешные
        List<Transaction> successfulTransactions = allTransactions.stream()
                .filter(t -> t.getStatus().equals(TransactionStatus.SUCCESS))
                .toList();

        return transactionMapper.toResponseList(successfulTransactions);
    }

    public BigDecimal calculateTotalIncoming(Long accountId) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        // Использовать repository метод для входящих транзакций
        BigDecimal total = transactionRepository.getTotalAmountByAccountAndStatus(
                accountId, TransactionStatus.SUCCESS);

        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalOutgoing(Long accountId) {
        // Проверить существование счета
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        // Получить все исходящие успешные транзакции
        List<Transaction> outgoingTransactions = transactionRepository
                .findAllByFromAccountId(accountId).stream()
                .filter(t -> t.getStatus().equals(TransactionStatus.SUCCESS))
                .toList();

        // Посчитать сумму
        return outgoingTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}