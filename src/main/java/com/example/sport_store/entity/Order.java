package com.example.sport_store.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Timestamp orderCreateDate;
    @Column
    private Timestamp orderCloseDate;
    @Column(nullable = false)
    private double orderSum;
    @ManyToOne
    private Customer customer;
    @ManyToOne
    private PaymentType paymentType;
    @ManyToOne
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderPosition> orderPositions;

    public Order() {
    }

    public Order(Timestamp orderCreateDate, Timestamp orderCloseDate, double orderSum, Customer customer,
                 PaymentType paymentType, OrderStatus orderStatus, List<OrderPosition> orderPositions) {
        this.orderCreateDate = orderCreateDate;
        this.orderCloseDate = orderCloseDate;
        this.orderSum = orderSum;
        this.customer = customer;
        this.paymentType = paymentType;
        this.orderStatus = orderStatus;
        this.orderPositions = orderPositions;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(Timestamp orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public Timestamp getOrderCloseDate() {
        return orderCloseDate;
    }

    public void setOrderCloseDate(Timestamp orderCloseDate) {
        this.orderCloseDate = orderCloseDate;
    }

    public double getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(double orderSum) {
        this.orderSum = orderSum;
    }

    public Customer getUser() {
        return customer;
    }

    public void setUser(Customer customer) {
        this.customer = customer;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderPosition> getOrderPositions() {
        return orderPositions;
    }

    public void setOrderPositions(List<OrderPosition> orderPositions) {
        this.orderPositions = orderPositions;
    }
}
