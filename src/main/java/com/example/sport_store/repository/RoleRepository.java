package com.example.sport_store.repository;

import com.example.sport_store.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRoleByName(String roleName);
}
