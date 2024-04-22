package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Address;
import com.shopping.Ecommerce.entity.Customer;
import com.shopping.Ecommerce.exception.CommonResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.repository.CustomerRepository;
import com.shopping.Ecommerce.request.CustomerLoginRequest;
import com.shopping.Ecommerce.request.CustomerSignUpRequest;
import com.shopping.Ecommerce.response.CustomerLoginResponse;
import com.shopping.Ecommerce.response.CustomerSignUpResponse;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    EmailService emailService;

    public ServiceResponse<CustomerSignUpResponse> signUpCustomer(CustomerSignUpRequest request) {
        String email = request.getEmail();
        if (customerRepository.findByEmail(email) != null) {
            return new ServiceResponse<>(null, "Customer with email " + email + " already exists.", HttpStatus.BAD_REQUEST);
        }
        Random r = new Random();
        String otp = String.format("%06d", r.nextInt(100000));

        Customer newCustomer = new Customer();
        newCustomer.setName(request.getName());
        newCustomer.setEmail(email);
        newCustomer.setPassword(request.getPassword());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setOtp(otp);
        newCustomer.setVerified(false);

        List<Address> addresses = new ArrayList<>();

        if (request.getAddresses() != null) {
            for (Address address : request.getAddresses()) {
                address.setCustomer(newCustomer);
                addresses.add(address);
            }
        }

        newCustomer.setAddresses(addresses);

        Customer savedCustomer = customerRepository.save(newCustomer);

        String subject = "Email Verfication";
        String body = "Thank you for signing up with our E-commerce website! Your verification OTP is " + savedCustomer.getOtp() + ". Please use this OTP to complete the verification process. Please do not share this OTP with anyone else for security reasons.";
        emailService.sendEmail(email, subject, body);

        CustomerSignUpResponse response = new CustomerSignUpResponse();
        response.setName(savedCustomer.getName());
        response.setPhone(savedCustomer.getPhone());
        response.setEmail(savedCustomer.getEmail());
        response.setAddresses(savedCustomer.getAddresses());
        response.setMessage("OTP sent successfully!");

        return new ServiceResponse<>(response, "SignUp successfully", HttpStatus.OK);
    }




    public ServiceResponse<CustomerLoginResponse> customerLogin(CustomerLoginRequest request){
        Customer customer = customerRepository.findByEmail(request.getEmail());
        if (customer == null || !passwordEncoder.matches(request.getPassword(), customer.getPassword()))  {
            return new ServiceResponse<>(null, "Invalid email or password.", HttpStatus.BAD_REQUEST);
        }
        if(!customer.isVerified()){
            return new ServiceResponse<>(null, "Verify first by giving otp.", HttpStatus.BAD_REQUEST);

        }

        String token = generateToken(customer);

        String capitalizedFirstName = capitalizeFirstLetter(customer.getName());

        CustomerLoginResponse response = new CustomerLoginResponse(token, capitalizedFirstName);

        return new ServiceResponse<>(response, "Login successfully", HttpStatus.OK);

    }

    public CommonResponse customerLogout(HttpServletRequest req, HttpServletResponse res){
        String token = req.getHeader("Authorization");
        Customer customer = authService.getUserFromToken(token);

        if (customer != null) {
            customerRepository.updateToken(customer.getId(), null);
            Cookie tokenCookie = new Cookie("token", null);
            tokenCookie.setMaxAge(0);
            tokenCookie.setPath("/");
            res.addCookie(tokenCookie);
            CommonResponse response = new CommonResponse(HttpStatus.OK, "Logout successful!");
            return response;
        } else {
            CommonResponse response = new CommonResponse(HttpStatus.UNAUTHORIZED, "Invalid Token or Network related Issue!");
            return response;
        }
    }

    public ServiceResponse<String> verifyUser(String email, String otp) {
        Customer customer = customerRepository.findByEmail(email);

        if(customer != null && customer.isVerified()) {
            return new ServiceResponse<>(null, "User Already Verified", HttpStatus.OK);
        }else if(otp.equals(customer.getOtp())) {
            customer.setVerified(true);
            customerRepository.save(customer);
            return new ServiceResponse<>(null, "Login successfully", HttpStatus.OK);
        }else {
            return new ServiceResponse<>(null, "User not Verified", HttpStatus.OK);
        }


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
