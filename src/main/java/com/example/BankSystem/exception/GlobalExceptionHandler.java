package com.example.BankSystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex,
                                                                         WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        log.error("Ошибка - пользователь не найден:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex,
                                                                        WebRequest request) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));
        log.error("Ошибка - аккаунт не найден:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFoundException(CardNotFoundException ex,
                                                                     WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));
        log.error("Ошибка - карта не найден:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex,
                                                                            WebRequest request) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        log.error("Ошибка - транзакция не найдена:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(
            InsufficientFundsException ex,
            WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));
        log.error("Ошибка - недостаточно средств:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionException(
            InvalidTransactionException ex,
            WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));
        log.error("Ошибка транзакции  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex,
                                                                         WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        log.error("Ошибка операции  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                         WebRequest request) {

        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        log.error("Ошибка валидации аргумента  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception ex,
                                                                  WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
                );

        log.error("Ошибка:  {} \n {} \n {} \n {} \n {}", error.getMessage(),
                error.getPath(), error.getStatus(), error.getStatusCode(),
                error.getTimestamp());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}

