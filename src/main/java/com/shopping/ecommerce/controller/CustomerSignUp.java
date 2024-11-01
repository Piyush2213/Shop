package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.request.CustomerSignUpRequest;
import com.shopping.ecommerce.response.CustomerSignUpResponse;
import com.shopping.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class CustomerSignUp {
    private final CustomerService customerService;

    @Autowired
    public CustomerSignUp(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ServiceResponse<CustomerSignUpResponse>> createNewCustomer(@RequestBody CustomerSignUpRequest customerSignUpRequest){
        ServiceResponse<CustomerSignUpResponse> response = customerService.signUpCustomer(customerSignUpRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ServiceResponse<String>> verifyUser(@RequestParam String email, @RequestParam String otp){
        ServiceResponse<String> res = customerService.verifyUser(email, otp);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
