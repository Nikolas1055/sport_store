package com.example.sport_store.repository;

import com.example.sport_store.entity.OrderPosition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderPositionRepository extends CrudRepository<OrderPosition, Long> {
    @Query(value = "select op from OrderPosition op where op.productAttribute.product.name like ?1")
    List<OrderPosition> findOrderPositionByProductName(String productName);
}
