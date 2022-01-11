package com.example.sport_store.service;

import com.example.sport_store.entity.AssignedRole;
import com.example.sport_store.entity.Customer;
import com.example.sport_store.repository.AssignedRoleRepository;
import com.example.sport_store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    AssignedRoleRepository assignedRoleRepository;


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Customer customer = customerRepository.findUserByLogin(login);
        if (customer == null) {
            throw new UsernameNotFoundException("Login " + login + " was not found in the database.");
        }
        List<AssignedRole> assignedRoles = assignedRoleRepository.findAssignedRolesByCustomer(customer);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        assignedRoles
                .forEach(assignedRole ->
                        grantedAuthorities.add(new SimpleGrantedAuthority(assignedRole.getRole().getName())));
        return new User(login,
                customer.getPassword(),
                true,
                true,
                true,
                customer.isActive(),
                grantedAuthorities);
    }
}
