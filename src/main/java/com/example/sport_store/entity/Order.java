package com.example.sport_store.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    public Customer getUser() {
        return customer;
    }

    public void setUser(Customer customer) {
        this.customer = customer;
    }
}
