package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Address;
import com.shopping.ecommerce.entity.Customer;
import com.shopping.ecommerce.exception.CommonResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.request.CustomerLoginRequest;
import com.shopping.ecommerce.request.CustomerSignUpRequest;
import com.shopping.ecommerce.response.CustomerLoginResponse;
import com.shopping.ecommerce.response.CustomerSignUpResponse;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
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
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid email or password.";

    private final CustomerRepository customerRepository;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Random random;

    private final ModelMapper modelMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AuthService authService,
                           BCryptPasswordEncoder passwordEncoder, EmailService emailService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.random = new Random();
    }

    public ServiceResponse<CustomerSignUpResponse> signUpCustomer(CustomerSignUpRequest request) {
        String email = request.getEmail();
        if (customerRepository.findByEmail(email) != null) {
            return new ServiceResponse<>(null, "Customer with email " + email + " already exists.", HttpStatus.BAD_REQUEST);
        }

        String otp = String.format("%06d", random.nextInt(100000));

        Customer newCustomer = modelMapper.map(request, Customer.class);
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

        String subject = "Email Verification";
        String body = "Thank you for signing up with our E-commerce website! Your verification OTP is " + savedCustomer.getOtp() + ". Please use this OTP to complete the verification process. Please do not share this OTP with anyone else for security reasons.";
        emailService.sendEmail(email, subject, body);



        CustomerSignUpResponse response = modelMapper.map(savedCustomer, CustomerSignUpResponse.class);
        response.setMessage("OTP sent successfully!");

        return new ServiceResponse<>(response, "SignUp successfully", HttpStatus.OK);
    }

    public ServiceResponse<CustomerLoginResponse> customerLogin(CustomerLoginRequest request){
        Customer customer = customerRepository.findByEmail(request.getEmail());
        if (customer == null || !passwordEncoder.matches(request.getPassword(), customer.getPassword()))  {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED);
        }
        if(!customer.isVerified()){
            return new ServiceResponse<>(null, "Verify first by giving otp.", HttpStatus.BAD_REQUEST);

        }

        String token = generateToken(customer);

        CustomerLoginResponse response = new CustomerLoginResponse(token, capitalizeFirstLetter(customer.getName()));

        return new ServiceResponse<>(response, "Login successfully", HttpStatus.OK);
    }

    public CommonResponse customerLogout(HttpServletRequest req, HttpServletResponse res){
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = authService.getUserFromToken(token);
        if (token == null) {
            return new CommonResponse(HttpStatus.BAD_REQUEST, "Token not provided.");
        }

        if (customer != null) {
            customerRepository.updateToken(customer.getId(), null);
            Cookie tokenCookie = new Cookie("token", null);
            tokenCookie.setMaxAge(0);
            tokenCookie.setPath("/");
            res.addCookie(tokenCookie);
            return new CommonResponse(HttpStatus.OK, "Logout successful!");
        } else {
            return new CommonResponse(HttpStatus.UNAUTHORIZED, "Invalid Token or Network related Issue!");
        }
    }

    public ServiceResponse<String> verifyUser(String email, String otp) {
        Customer customer = customerRepository.findByEmail(email);

        if(customer != null && customer.isVerified()) {
            return new ServiceResponse<>(null, "User Already Verified", HttpStatus.OK);
        }else if(customer!=null && otp.equals(customer.getOtp())) {
            customer.setVerified(true);
            customerRepository.save(customer);
            return new ServiceResponse<>(null, "Verified successfully", HttpStatus.OK);
        }else {
            return new ServiceResponse<>(null, "User not Verified", HttpStatus.BAD_REQUEST);
        }
    }

    private String generateToken(Customer customer) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
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
