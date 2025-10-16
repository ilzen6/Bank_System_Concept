package com.example.BankSystem.dto.MapStructs;

import com.example.BankSystem.dto.response.TransactionResponse;
import com.example.BankSystem.dto.response.TransferResponse;
import com.example.BankSystem.entity.Account;
import com.example.BankSystem.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TransactionMapper {

    @Mapping(target = "fromAccountNumber",
            expression = "java(transaction.getFromAccount() != null" +
                    " ? transaction.getFromAccount().getAccountNumber() : null)")
    @Mapping(target = "toAccountNumber",
            expression = "java(transaction.getToAccount() != null" +
                    " ? transaction.getToAccount().getAccountNumber() : null)")
    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    @Mapping(target = "success", constant = "true")
    @Mapping(target = "message", constant = "Transfer completed successfully")
    @Mapping(source = "transaction", target = "transaction")
    @Mapping(target = "fromAccountBalance", expression = "java(fromAccount.getBalance())")
    @Mapping(target = "toAccountBalance", expression = "java(toAccount.getBalance())")
    TransferResponse toTransferResponse(Transaction transaction, Account fromAccount, Account toAccount);
}
