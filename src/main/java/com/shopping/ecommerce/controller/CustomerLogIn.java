package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.exception.CommonResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.request.CustomerLoginRequest;
import com.shopping.ecommerce.response.CustomerLoginResponse;
import com.shopping.ecommerce.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class CustomerLogIn {
    private final CustomerService customerService;


    @Autowired
    public CustomerLogIn(CustomerService customerService) {
        this.customerService = customerService;
    }

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
