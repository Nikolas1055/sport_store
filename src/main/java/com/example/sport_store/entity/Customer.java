package com.example.sport_store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "sport_store", catalog = "postgres")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, max = 100, message = "Имя должно быть от 3 до 100 символов длиной")
    @Column(nullable = false, length = 100)
    private String name;
    @Size(min = 3, max = 100, message = "Фамилия должна быть от 3 до 100 символов длиной")
    @Column(nullable = false, length = 100)
    private String surname;
    @Size(min = 3, max = 100, message = "Логин должен быть от 3 до 100 символов длиной")
    @Column(unique = true, nullable = false, length = 100)
    private String login;
    @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
    @Column(nullable = false)
    private String password;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @Size(min = 7, max = 20, message = "Номер телефона должен быть от 7 до 20 символов длиной")
    @Column(unique = true, nullable = false, length = 20)
    private String phone;
    @Column(nullable = false)
    private Date regDate;
    @Column(nullable = false)
    private String photo;
    @Size(min = 3, max = 100, message = "Название города должно быть от 3 до 100 символов длиной")
    @Column(nullable = false, length = 100)
    private String city;
    @Size(min = 3, max = 100, message = "Название страны должно быть от 3 до 100 символов длиной")
    @Column(nullable = false, length = 100)
    private String country;
    @Column(nullable = false)
    private boolean isActive;
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Customer() {
        isActive = true;
    }
}
