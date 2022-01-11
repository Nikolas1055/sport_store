package com.example.sport_store.repository;

import com.example.sport_store.entity.OrderPosition;
import org.springframework.data.repository.CrudRepository;

public interface OrderPositionRepository extends CrudRepository<OrderPosition, Long> {
}
