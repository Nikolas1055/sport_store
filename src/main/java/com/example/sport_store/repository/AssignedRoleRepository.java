package com.example.sport_store.repository;

import com.example.sport_store.entity.AssignedRole;
import com.example.sport_store.entity.Customer;
import com.example.sport_store.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignedRoleRepository extends JpaRepository<AssignedRole, Long> {
    List<AssignedRole> findAssignedRolesByCustomer(Customer customer);

    AssignedRole findAssignedRoleByCustomer(Customer customer);

    Page<AssignedRole> findAssignedRolesByRole(Role role, Pageable pageable);

    @Query(value = "select ar from AssignedRole ar where ar.role.name not like ?1 and ar.customer.login not like ?2")
    Page<AssignedRole> findAssignedRolesWithoutRoleUserAndPrincipal(String roleName, String login, Pageable pageable);
}
