package com.example.BankSystem.dto.MapStructs;


import com.example.BankSystem.dto.request.CreateCardRequest;
import com.example.BankSystem.dto.response.CardResponse;
import com.example.BankSystem.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CardMapper {

    @Mapping(source = "maskCardNumber", target = "maskedCardNumber")
    CardResponse toResponse(Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "cvv", ignore = true)
    @Mapping(target = "pinCode", ignore = true)
    @Mapping(target = "expiryDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isExpired", ignore = true)
    @Mapping(target = "isValid", ignore = true)
    @Mapping(target = "maskCardNumber", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "issueDate", expression ="java(LocalDate.now())")
    Card toEntity(CreateCardRequest request);

    List<CardResponse> toResponseList(List<Card> cards);

}
