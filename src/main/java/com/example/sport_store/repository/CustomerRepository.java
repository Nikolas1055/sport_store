package com.example.sport_store.repository;

import com.example.sport_store.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findUserByLogin(String login);
    Customer getCustomerById(Long id);
}
