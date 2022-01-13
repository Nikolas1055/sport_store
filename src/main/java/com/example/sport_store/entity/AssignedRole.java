package com.example.sport_store.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "sport_store", catalog = "postgres", name = "assigned_role",
        uniqueConstraints = {
                @UniqueConstraint(name = "CUSTOMER_ROLE_UK", columnNames = {"customer_id", "role_id"})})
public class AssignedRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public AssignedRole(Customer customer, Role role) {
        this.customer = customer;
        this.role = role;
    }
}
