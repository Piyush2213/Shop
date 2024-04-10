package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.entity.Customer;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.exception.ServiceResponse;
import com.shopping.Ecommerce.repository.CustomerRepository;
import com.shopping.Ecommerce.request.CustomerLoginRequest;
import com.shopping.Ecommerce.response.CustomerLogOutResponse;
import com.shopping.Ecommerce.response.CustomerLoginResponse;
import com.shopping.Ecommerce.service.AuthService;
import com.shopping.Ecommerce.service.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})

public class CustomerLogIn {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<CustomerLoginResponse>> customerLogin(@RequestBody CustomerLoginRequest customerLoginRequest){

        ServiceResponse<CustomerLoginResponse> serviceResponse = customerService.customerLogin(customerLoginRequest);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<CustomerLogOutResponse> logout(HttpServletRequest req, HttpServletResponse res) {
        try {
            String token = req.getHeader("Authorization");
            Customer customer = authService.getUserFromToken(token);

            if (customer != null) {
                customerRepository.updateToken(customer.getId(), null);
                Cookie tokenCookie = new Cookie("token", null);
                tokenCookie.setMaxAge(0);
                tokenCookie.setPath("/");
                res.addCookie(tokenCookie);
                CustomerLogOutResponse response = new CustomerLogOutResponse("Logout successful!");

                return ResponseEntity.ok().body(response);
            } else {
                throw new ExistsException("Invalid token");
            }
        } catch (ExistsException ex) {
            CustomerLogOutResponse errResponse = new CustomerLogOutResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errResponse);
        }
    }





}
