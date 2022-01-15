package com.example.sport_store.repository;

import com.example.sport_store.entity.Color;
import com.example.sport_store.entity.Product;
import com.example.sport_store.entity.ProductAttribute;
import com.example.sport_store.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> getProductAttributesByProduct(Product product);

    Optional<ProductAttribute> findProductAttributeByProductAndColorAndSize(Product product, Color color, Size size);

    Optional<ProductAttribute> findProductAttributeBySize(Size size);

    Optional<ProductAttribute> findProductAttributeByColor(Color color);

    @Query(value = "select pa from ProductAttribute pa where pa.color.name like ?1")
    List<ProductAttribute> findAllByColorName(String colorName);

    @Query(value = "select pa from ProductAttribute pa where pa.size.name like ?1")
    List<ProductAttribute> findAllBySizeName(String sizeName);
}
