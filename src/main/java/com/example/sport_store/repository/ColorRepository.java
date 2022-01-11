package com.example.sport_store.repository;

import com.example.sport_store.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Color getColorById(Long id);
}
