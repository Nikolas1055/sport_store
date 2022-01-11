package com.example.sport_store.entity;

import javax.persistence.*;
import java.io.Serializable;

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

    public ProductAttribute() {
    }

    public ProductAttribute(Product product, Color color, Size size, int quantity) {
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductAttribute productAttribute = (ProductAttribute) o;
        return this.id.equals(productAttribute.id);
    }

    @Override
    public int hashCode() {
        return this.id.intValue();
    }
}
