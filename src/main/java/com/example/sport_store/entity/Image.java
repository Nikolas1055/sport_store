package com.example.sport_store.entity;

import javax.persistence.*;

@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String image;
    @ManyToOne
    private Product product;

    public Image() {
    }

    public Image(String image, Product product) {
        this.image = image;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
