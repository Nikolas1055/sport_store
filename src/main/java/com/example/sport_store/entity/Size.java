package com.example.sport_store.entity;

import javax.persistence.*;

@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 10)
    private String name;

    public Size() {
    }

    public Size(String name) {
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
