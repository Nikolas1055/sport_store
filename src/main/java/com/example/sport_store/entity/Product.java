package com.example.sport_store.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    @Size(min = 3, max = 4000)
    @Column(length = 4000)
    private String description;
    @Column(nullable = false)
    private double price;
    private int rating;
    @ManyToOne
    private Category category;
    @ManyToOne
    private Manufacturer manufacturer;
    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Image> images;
    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductAttribute> productAttributes;
}