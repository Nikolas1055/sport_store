package com.example.sport_store.entity;

import javax.persistence.*;

@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class PaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    public PaymentType() {
    }

    public PaymentType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
