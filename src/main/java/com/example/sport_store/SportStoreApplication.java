package com.example.sport_store;

import com.example.sport_store.entity.AssignedRole;
import com.example.sport_store.entity.Customer;
import com.example.sport_store.entity.Role;
import com.example.sport_store.repository.AssignedRoleRepository;
import com.example.sport_store.repository.CustomerRepository;
import com.example.sport_store.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Date;

@SpringBootApplication
public class SportStoreApplication {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AssignedRoleRepository assignedRoleRepository;

    public static void main(String[] args) {
        SpringApplication.run(SportStoreApplication.class, args);
    }

    @PostConstruct
    private void init() {
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_MANAGER"));
            roleRepository.save(new Role("ROLE_CASHIER"));
            roleRepository.save(new Role("ROLE_USER"));
        }
        if (customerRepository.findAll().isEmpty()) {
            Customer customer = new Customer();
            customer.setName("Admin");
            customer.setSurname("Admin");
            customer.setCity("NN");
            customer.setCountry("RU");
            customer.setEmail("email@server.com");
            customer.setLogin("admin");
            customer.setPassword(new BCryptPasswordEncoder().encode("admin"));
            customer.setPhone("89998887755");
            customer.setRegDate(new Date());
            customerRepository.save(customer);
            assignedRoleRepository.save(new AssignedRole(customer, roleRepository.getById(1L)));
        }
    }
}
