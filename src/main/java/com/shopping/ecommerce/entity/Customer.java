package com.shopping.ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @Column(name = "token")
    private String token;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Cart cart;

    private String otp;
    @Column(name = "verified")
    private boolean verified;

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }




}
