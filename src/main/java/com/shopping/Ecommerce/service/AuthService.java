package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Admin;
import com.shopping.Ecommerce.entity.Customer;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.repository.AdminRepository;
import com.shopping.Ecommerce.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;


    @Transactional
    public Customer getUserFromToken(String token) {
        Customer customer = customerRepository.findByToken(token);
        if (customer == null) {
            throw new ExistsException("Invalid token or user not found.");
        }
        return customer;
    }
    public boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        Admin admin = adminRepository.findByToken(token);
        return admin != null && "Admin".equals(admin.getRole());
    }
}

