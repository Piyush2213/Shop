package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Customer;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.exception.ServiceResponse;
import com.shopping.Ecommerce.repository.CustomerRepository;
import com.shopping.Ecommerce.request.CustomerLoginRequest;
import com.shopping.Ecommerce.request.CustomerSignUpRequest;
import com.shopping.Ecommerce.response.CustomerLoginResponse;
import com.shopping.Ecommerce.response.CustomerSignUpResponse;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerSignUpResponse signUpCustomer(CustomerSignUpRequest request) {
        String email = request.getEmail();
        if (customerRepository.findByEmail(email) != null) {
            throw new ExistsException("Customer with email " + email + " already exists.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setName(request.getName());
        newCustomer.setEmail(email);
        newCustomer.setPassword(request.getPassword());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(newCustomer);

        CustomerSignUpResponse response = new CustomerSignUpResponse();
        response.setMessage("Added successfully");

        return response;
    }



    public ServiceResponse<CustomerLoginResponse> customerLogin(CustomerLoginRequest request){
        Customer customer = customerRepository.findByEmail(request.getEmail());
        if (customer == null || !customer.getPassword().equals(request.getPassword())) {
            return new ServiceResponse<>(null, "Invalid email or password.", HttpStatus.BAD_REQUEST);
        }

        String token = generateToken(customer);

        String capitalizedFirstName = capitalizeFirstLetter(customer.getName());

        CustomerLoginResponse response = new CustomerLoginResponse(token, capitalizedFirstName);

        return new ServiceResponse<>(response, "Login successfully", HttpStatus.OK);

    }



    private String generateToken(Customer customer) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String firstName = capitalizeFirstLetter(customer.getName());
        String token = Jwts.builder()

                .setSubject(customer.getEmail())
                .claim("name", customer.getName())
                .claim("role", "customer")
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();

        customer.setToken(token);
        customerRepository.updateToken(customer.getId(), token);

        return token;
    }
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
