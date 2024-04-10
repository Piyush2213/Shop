package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.request.CustomerSignUpRequest;
import com.shopping.Ecommerce.response.CustomerSignUpResponse;
import com.shopping.Ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class CustomerSignUp {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/signup")
    public ResponseEntity<CustomerSignUpResponse> createNewCustomer(@RequestBody CustomerSignUpRequest customerSignUpRequest){
        try {
            CustomerSignUpResponse response = customerService.signUpCustomer(customerSignUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ExistsException ex) {
            CustomerSignUpResponse errorResponse = new CustomerSignUpResponse();
            errorResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }
}
