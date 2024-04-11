package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.exception.CommonResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.repository.CustomerRepository;
import com.shopping.Ecommerce.request.CustomerLoginRequest;
import com.shopping.Ecommerce.response.CustomerLoginResponse;
import com.shopping.Ecommerce.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})

public class CustomerLogIn {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<CustomerLoginResponse>> customerLogin(@RequestBody CustomerLoginRequest customerLoginRequest){

        ServiceResponse<CustomerLoginResponse> serviceResponse = customerService.customerLogin(customerLoginRequest);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(HttpServletRequest req, HttpServletResponse res) {
        CommonResponse response = customerService.customerLogout(req,res);
        return ResponseEntity.status(response.getStatus()).body(response);
    }





}
