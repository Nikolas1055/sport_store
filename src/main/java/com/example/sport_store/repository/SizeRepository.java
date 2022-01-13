package com.example.sport_store.repository;

import com.example.sport_store.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Size getSizeById(Long id);

    Optional<Size> findSizeByName(String name);
}
