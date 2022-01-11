package com.example.sport_store.repository;

import com.example.sport_store.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Size getSizeById(Long id);
}
