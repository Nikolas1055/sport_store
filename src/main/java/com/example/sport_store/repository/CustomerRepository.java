package com.example.sport_store.repository;

import com.example.sport_store.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findUserByLogin(String login);

    Optional<Customer> findCustomerByLogin(String login);

    Optional<Customer> findCustomerByEmail(String email);

    Optional<Customer> findCustomerByPhone(String phone);

    Customer getCustomerById(Long id);
}
