package com.example.sport_store.repository;

import com.example.sport_store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category getCategoryById(Long id);

    Optional<Category> findCategoryByName(String name);

    Optional<Category> findCategoryById(Long id);
}
