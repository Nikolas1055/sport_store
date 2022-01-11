package com.example.sport_store.repository;

import com.example.sport_store.entity.Category;
import com.example.sport_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product getProductById(Long id);
    List<Product> getProductsByCategory(Category category);
    List<Product> findTop5ByOrderByRatingDesc();
    List<Product> findAllByNameContains(String name);
}
