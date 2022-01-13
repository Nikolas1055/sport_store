package com.example.sport_store.repository;

import com.example.sport_store.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    PaymentType getPaymentTypeById(Long id);

    Optional<PaymentType> findPaymentTypeByName(String name);
}
