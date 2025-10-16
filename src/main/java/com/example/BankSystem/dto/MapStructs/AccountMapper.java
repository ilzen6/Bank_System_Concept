package com.example.BankSystem.dto.MapStructs;


import com.example.BankSystem.dto.request.CreateAccountRequest;
import com.example.BankSystem.dto.response.AccountDetailResponse;
import com.example.BankSystem.dto.response.AccountResponse;
import com.example.BankSystem.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface AccountMapper {

    @Mapping(target = "isActive",
            expression = "java(account.status.equals(com.example." +
                    "BankSystem.enums.AccountStatus.ACTIVE) ? true : false)")
    AccountResponse toResponse(Account account);

    @Mapping(target = "isActive",
            expression = "java(account.status.equals(com.example." +
                    "BankSystem.enums.AccountStatus.ACTIVE) ? true : false)")
    @Mapping(target = "displayInfo", expression = "java(buildDisplayInfo(account))")
    @Mapping(source = "customer.fullName", target = "customerFullName")
    @Mapping(target = "cardsCount",
            expression = "java(account.getCards() != null ? account.getCards().size() : 0)")
    AccountDetailResponse toDetailResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "displayInfo", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "outgoingTransactions", ignore = true)
    @Mapping(target = "incomingTransactions", ignore = true)
    @Mapping(target = "balance", constant = "0")
    @Mapping(target = "status", constant = "AccountStatus.ACTIVE")
    @Mapping(target = "openDate", expression = "java(LocalDate.now())")
    Account toEntity(CreateAccountRequest request);

    List<AccountResponse> toResponseList(List<Account> accounts);

    default String buildDisplayInfo(Account account) {
        return "Счет " + account.getAccountNumber() + "-" + account.getAccountType()
                + "(" + account.getBalance() + " " + account.getCurrency() + ")";
    }

}
