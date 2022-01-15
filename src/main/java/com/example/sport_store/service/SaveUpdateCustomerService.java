package com.example.sport_store.service;

import com.example.sport_store.entity.AssignedRole;
import com.example.sport_store.entity.Customer;
import com.example.sport_store.repository.AssignedRoleRepository;
import com.example.sport_store.repository.CustomerRepository;
import com.example.sport_store.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
public class SaveUpdateCustomerService {
    private static final String UPLOAD_FOLDER = "upload/customer_image/";
    final CustomerRepository customerRepository;
    final AssignedRoleRepository assignedRoleRepository;
    final RoleRepository roleRepository;

    @Autowired
    public SaveUpdateCustomerService(CustomerRepository customerRepository, AssignedRoleRepository assignedRoleRepository, RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.assignedRoleRepository = assignedRoleRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Метод для сохранения нового покупателя/сотрудника в БД.
     */
    public void saveNewCustomer(MultipartFile image, Customer customer, String password, String roleId) {
        if (!image.isEmpty()) {
            uploadImage(image, customer);
        } else {
            customer.setPhoto("/" + UPLOAD_FOLDER + "no_avatar.png");
        }
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customer.setRegDate(new Date());
        customerRepository.save(customer);
        assignedRoleRepository.save(
                new AssignedRole(customer, roleId == null ?
                        roleRepository.getRoleByName("ROLE_USER") :
                        roleRepository.getById(Long.parseLong(roleId))));

    }

    /**
     * Метод для обновления данных покупателя/сотрудника в БД.
     */
    public void updateExistCustomer(Customer existCustomer, MultipartFile image, String name, String surname, String phone,
                                    String email, String country, String city, String login, String roleId) {
        Customer customer = customerRepository.getCustomerById(existCustomer.getId());
        if (!image.isEmpty()) {
            uploadImage(image, customer);
        }
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCountry(country);
        customer.setCity(city);
        customer.setLogin(login);
        customerRepository.save(customer);
        if (roleId != null) {
            AssignedRole assignedRole = assignedRoleRepository.findAssignedRoleByCustomer(customer);
            assignedRole.setRole(roleRepository.getById(Long.parseLong(roleId)));
            assignedRoleRepository.save(assignedRole);
        }
    }

    /**
     * Метод для сохранения изображения покупателя/сотрудника.
     */
    private void uploadImage(MultipartFile image, Customer customer) {
        try {
            String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                    image.getOriginalFilename();
            Files.copy(image.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
            customer.setPhoto("/" + UPLOAD_FOLDER + newImageFileName.replaceAll(" ", ""));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}
