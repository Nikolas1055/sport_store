package com.example.sport_store.service;

import com.example.sport_store.entity.Customer;
import com.example.sport_store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class CustomValidationService {
    private static final String OBJECT_NAME = "customer";
    private static final String FIELD_LOGIN = "login";
    private static final String LOGIN_ERROR = "Пользователь с таким логином уже существует";
    private static final String FIELD_EMAIL = "email";
    private static final String EMAIL_ERROR = "Пользователь с таким почтовым адресом уже существует";
    private static final String FIELD_PHONE = "phone";
    private static final String PHONE_ERROR = "Пользователь с таким номером телефона уже существует";
    final CustomerRepository customerRepository;

    @Autowired
    public CustomValidationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Метод для проверки на уникальность полей.
     */
    public BindingResult checkCustomerUniqueFields(BindingResult result, String login, String phone, String email, Long id) {
        if (check(customerRepository.findCustomerByLogin(login).orElse(null), id)) {
            result.addError(new FieldError(OBJECT_NAME, FIELD_LOGIN, LOGIN_ERROR));
        }
        if (check(customerRepository.findCustomerByEmail(email).orElse(null), id)) {
            result.addError(new FieldError(OBJECT_NAME, FIELD_EMAIL, EMAIL_ERROR));
        }
        if (check(customerRepository.findCustomerByPhone(phone).orElse(null), id)) {
            result.addError(new FieldError(OBJECT_NAME, FIELD_PHONE, PHONE_ERROR));
        }
        return result;
    }

    private boolean check(Customer customer, Long id) {
        return (customer != null && id == null) || (customer != null && !customer.getId().equals(id));
    }
}
