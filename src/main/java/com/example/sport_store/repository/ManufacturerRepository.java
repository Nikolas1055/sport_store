package com.example.sport_store.repository;

import com.example.sport_store.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    Manufacturer getManufacturerById(Long id);

    Optional<Manufacturer> findManufacturerByName(String name);
}
