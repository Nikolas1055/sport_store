package com.example.sport_store.repository;

import com.example.sport_store.entity.Category;
import com.example.sport_store.entity.Manufacturer;
import com.example.sport_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product getProductById(Long id);

    List<Product> getProductsByCategory(Category category);

    List<Product> findTop5ByOrderByRatingDesc();

    List<Product> findAllByNameContains(String name);

    @Query(value = "select p from Product p where p.category.name like ?1")
    List<Product> findAllByCategoryName(String categoryName);

    @Query(value = "select p from Product p where p.manufacturer.name like ?1")
    List<Product> findAllByManufacturerName(String manufacturerName);

    List<Product> findAllByPrice(Double price);

    Optional<Product> findProductByManufacturer(Manufacturer manufacturer);

    Optional<Product> findProductByCategory(Category category);

    Optional<Product> findProductByName(String name);
}
