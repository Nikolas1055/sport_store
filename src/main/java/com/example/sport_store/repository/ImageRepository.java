package com.example.sport_store.repository;

import com.example.sport_store.entity.Image;
import com.example.sport_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> getImagesByProduct(Product product);
}
