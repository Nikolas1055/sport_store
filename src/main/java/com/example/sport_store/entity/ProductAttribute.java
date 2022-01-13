package com.example.sport_store.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    @ManyToOne
    private Color color;
    @ManyToOne
    private Size size;
    private int quantity;

    public ProductAttribute(Product product, Color color, Size size, int quantity) {
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }
}
