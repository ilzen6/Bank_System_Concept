package com.example.BankSystem.dto.MapStructs;

import com.example.BankSystem.dto.request.CreateCustomerRequest;
import com.example.BankSystem.dto.request.UpdateCustomerRequest;
import com.example.BankSystem.dto.response.CustomerDetailResponse;
import com.example.BankSystem.dto.response.CustomerResponse;
import com.example.BankSystem.entity.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN,
        uses = {AccountMapper.class, CardMapper.class})
public interface CustomerMapper {
    @Mapping(target = "fullName", expression = "java(buildFullName(customer))")
    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "fullName", expression = "java(buildFullName(customer))")
    @Mapping(target = "accountsCount",
            expression = "java(customer.getAccounts() != null ? customer.getAccounts().size() : 0)")
    @Mapping(target = "cardsCount",
            expression = "java(customer.getCards() != null ? customer.getCards().size() : 0)")
    CustomerDetailResponse toDetailResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "registrationDate", expression = "java(LocalDate.now())")
    Customer toEntity(CreateCustomerRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passportNumber", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
    default String buildFullName(Customer customer) {
        if (customer.getMiddleName() != null && !customer.getMiddleName().isEmpty()) {
            return customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName();
        }
        return customer.getFirstName() + " " + customer.getLastName();
    }
}