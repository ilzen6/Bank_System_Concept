package com.example.BankSystem.repository;

import com.example.BankSystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Optional<Customer> findByPassportNumber(String passportNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPassportNumber(String passportNumber);

    List<Customer> findAllByLastNameContainingIgnoreCase(String lastName);

    List<Customer> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<Customer> findAllByRegistrationDateAfter(LocalDate date);

    List<Customer> findAllByRegistrationDateBetween(LocalDate start, LocalDate end);

    long countByRegistrationDateAfter(LocalDate date);



}
