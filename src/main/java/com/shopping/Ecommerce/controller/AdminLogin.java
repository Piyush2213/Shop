package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.entity.Admin;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.request.AdminLoginRequest;
import com.shopping.Ecommerce.request.CustomerLoginRequest;
import com.shopping.Ecommerce.response.AdminLoginResponse;
import com.shopping.Ecommerce.response.CustomerLoginResponse;
import com.shopping.Ecommerce.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})

public class AdminLogin {
    @Autowired
    AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> response(@RequestBody AdminLoginRequest adminLoginRequest){
        try {
            AdminLoginResponse response = adminService.adminLogin(adminLoginRequest);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExistsException ex) {
            AdminLoginResponse errorResponse = new AdminLoginResponse();
            errorResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<String> logout(HttpServletRequest req, HttpServletResponse res) {
        try {
            String message = adminService.logout(req, res);
            return ResponseEntity.ok(message);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


}
