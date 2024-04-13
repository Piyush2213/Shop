package com.shopping.Ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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


}
