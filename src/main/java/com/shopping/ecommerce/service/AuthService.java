package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Admin;
import com.shopping.ecommerce.entity.Customer;
import com.shopping.ecommerce.repository.AdminRepository;
import com.shopping.ecommerce.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AuthService(CustomerRepository customerRepository, AdminRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }


    @Transactional
    public Customer getUserFromToken(String token) {
        Customer customer = customerRepository.findByToken(token);
        if (customer == null) {
            return null;
        }
        return customer;
    }
    public boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        Admin admin = adminRepository.findByToken(token);
        return admin != null && "Admin".equals(admin.getRole());
    }
}

