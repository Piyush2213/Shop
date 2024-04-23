package com.shopping.ecommerce.controller;


import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.request.AdminLoginRequest;
import com.shopping.ecommerce.response.AdminLoginResponse;
import com.shopping.ecommerce.service.AdminService;
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
    private final AdminService adminService;

    @Autowired
    public AdminLogin(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<AdminLoginResponse>> adminLogin(@RequestBody AdminLoginRequest adminLoginRequest){

        ServiceResponse<AdminLoginResponse> response = adminService.adminLogin(adminLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);

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
