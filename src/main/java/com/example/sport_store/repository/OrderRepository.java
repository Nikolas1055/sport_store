package com.example.sport_store.repository;

import com.example.sport_store.entity.Customer;
import com.example.sport_store.entity.Order;
import com.example.sport_store.entity.OrderStatus;
import com.example.sport_store.entity.PaymentType;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Order getOrderById(Long id);

    List<Order> findAllByOrderCloseDateIsNull();

    List<Order> findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNotNull();

    List<Order> findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNull();

    List<Order> findOrdersByPaymentTypeIsNotNullAndOrderCloseDateIsAfter(Timestamp timestamp);

    List<Order> findOrdersByCustomer(Customer customer);

    Optional<Order> findOrderByOrderStatus(OrderStatus orderStatus);

    Optional<Order> findOrderByPaymentType(PaymentType paymentType);
}
