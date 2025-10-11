package com.example.BankSystem.dto.MapStructs;

import com.example.BankSystem.dto.request.CreateCustomerRequest;
import com.example.BankSystem.dto.response.CustomerResponse;
import com.example.BankSystem.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(source = "firstName", target = "name")
    @Mapping(target = "fullName", expression = "java(customer.getFirstName \" \" + customer.getLastName")
    CustomerResponse toDTO(Customer customer);
    Customer toEntity(CreateCustomerRequest createCustomerRequest);

    List<CustomerResponse> toDTOList(List<Customer> customers);
    List<Customer> toEntityList(List<CreateCustomerRequest> customersRequestDTO);
}
