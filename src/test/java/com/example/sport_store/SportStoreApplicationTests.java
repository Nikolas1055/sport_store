package com.example.sport_store;

import com.example.sport_store.entity.Customer;
import com.example.sport_store.repository.CustomerRepository;
import com.example.sport_store.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SportStoreApplicationTests {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    RoleRepository roleRepository;

    @Test
    void contextLoadsSimple() {
    }

    @Test
    void contextLoadsCustomerRepository() {
        assertThat(customerRepository).isNotNull();
    }

    @Test
    public void getCustomerById() {
        Customer customer = customerRepository.getById(1L);
        assertThat(customer.getId()).isEqualTo(1);
    }

    @Test
    public void checkRoles() {
        assertThat(roleRepository.findAll().size()).isEqualTo(4);
    }
}
