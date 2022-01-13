package com.example.sport_store.repository;

import com.example.sport_store.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    OrderStatus getOrderStatusById(Long id);

    Optional<OrderStatus> findOrderStatusByName(String name);
}
