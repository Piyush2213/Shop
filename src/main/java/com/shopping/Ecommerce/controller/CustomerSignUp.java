package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.request.CustomerSignUpRequest;
import com.shopping.Ecommerce.response.CustomerSignUpResponse;
import com.shopping.Ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class CustomerSignUp {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResponse<CustomerSignUpResponse>>createNewCustomer(@RequestBody CustomerSignUpRequest customerSignUpRequest){
        ServiceResponse<CustomerSignUpResponse> response = customerService.signUpCustomer(customerSignUpRequest);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
}
