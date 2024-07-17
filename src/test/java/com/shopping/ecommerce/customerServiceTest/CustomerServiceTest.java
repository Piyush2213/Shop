package com.shopping.ecommerce.customerServiceTest;



import com.shopping.ecommerce.entity.Customer;
import com.shopping.ecommerce.exception.CommonResponse;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.request.CustomerLoginRequest;
import com.shopping.ecommerce.request.CustomerSignUpRequest;
import com.shopping.ecommerce.response.CustomerLoginResponse;
import com.shopping.ecommerce.response.CustomerSignUpResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.service.AuthService;
import com.shopping.ecommerce.service.CustomerService;
import com.shopping.ecommerce.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Method;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuthService authService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUpCustomer_ValidRequest_Test() {
        CustomerSignUpRequest request = new CustomerSignUpRequest();
        request.setEmail("customer@example.com");
        request.setPassword("password");

        CustomerSignUpResponse signUpResponse = new CustomerSignUpResponse();
        signUpResponse.setMessage("OTP sent successfully!");

        when(customerRepository.findByEmail("customer@example.com")).thenReturn(null);
        when(modelMapper.map(request, Customer.class)).thenReturn(new Customer());
        when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());
        when(modelMapper.map(any(Customer.class), eq(CustomerSignUpResponse.class))).thenReturn(signUpResponse);

        ServiceResponse<CustomerSignUpResponse> response = customerService.signUpCustomer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("SignUp successfully", response.getMessage());
        verify(emailService).sendEmail(eq("customer@example.com"), anyString(), contains("Your verification OTP is"));
    }

    @Test
    void signUpCustomer_ExistingEmail_Test() {
        CustomerSignUpRequest request = new CustomerSignUpRequest();
        request.setEmail("customer@example.com");
        request.setPassword("password");

        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("customer@example.com");

        when(customerRepository.findByEmail("customer@example.com")).thenReturn(existingCustomer);

        ServiceResponse<CustomerSignUpResponse> response = customerService.signUpCustomer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertNull(response.getData());
        assertEquals("Customer with email customer@example.com already exists.", response.getMessage());
    }




    @Test
    void customerLogin_InvalidCredentials_Test() {
        CustomerLoginRequest request = new CustomerLoginRequest();
        request.setEmail("customer@example.com");
        request.setPassword("wrongpassword");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword("encodedPassword");
        customer.setName("john");

        when(customerRepository.findByEmail("customer@example.com")).thenReturn(customer);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        ServiceResponse<CustomerLoginResponse> response = customerService.customerLogin(request);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertNull(response.getData());
        assertEquals("Invalid email or password.", response.getMessage());
    }



    @Test
    void customerLogout_ValidToken_Test() {
        String token = "validToken";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        Customer customer = new Customer();
        customer.setId(1);
        customer.setToken(token);

        when(req.getHeader("Authorization")).thenReturn(token);
        when(authService.getUserFromToken(token)).thenReturn(customer);

        CommonResponse response = customerService.customerLogout(req, res);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Logout successful!", response.getMessage());
        verify(customerRepository).updateToken(1, null);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(res).addCookie(cookieCaptor.capture());
        Cookie tokenCookie = cookieCaptor.getValue();
        assertEquals("token", tokenCookie.getName());
        assertNull(tokenCookie.getValue());
        assertEquals(0, tokenCookie.getMaxAge());
        assertEquals("/", tokenCookie.getPath());
    }

    @Test
    void customerLogout_InvalidToken_Test() {
        String token = "invalidToken";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        when(req.getHeader("Authorization")).thenReturn(token);
        when(authService.getUserFromToken(token)).thenReturn(null);

        CommonResponse response = customerService.customerLogout(req, res);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals("Invalid Token or Network related Issue!", response.getMessage());
    }

    @Test
    void customerLogout_NoTokenProvided_Test() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        when(req.getHeader("Authorization")).thenReturn(null);

        CommonResponse response = customerService.customerLogout(req, res);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Token not provided.", response.getMessage());
    }

    @Test
    void verifyUser_AlreadyVerified_Test() {
        String email = "customer@example.com";
        String otp = "123456";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setOtp(otp);
        customer.setVerified(true);

        when(customerRepository.findByEmail(email)).thenReturn(customer);

        ServiceResponse<String> response = customerService.verifyUser(email, otp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("User Already Verified", response.getMessage());
    }

    @Test
    void verifyUser_ValidOTP_Test() {
        String email = "customer@example.com";
        String otp = "123456";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setOtp(otp);
        customer.setVerified(false);

        when(customerRepository.findByEmail(email)).thenReturn(customer);

        ServiceResponse<String> response = customerService.verifyUser(email, otp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Verified successfully", response.getMessage());
        assertTrue(customer.isVerified());
        verify(customerRepository).save(customer);
    }

    @Test
    void verifyUser_InvalidOTP_Test() {
        String email = "customer@example.com";
        String otp = "wrongotp";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setOtp("123456");
        customer.setVerified(false);

        when(customerRepository.findByEmail(email)).thenReturn(customer);

        ServiceResponse<String> response = customerService.verifyUser(email, otp);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("User not Verified", response.getMessage());
        assertFalse(customer.isVerified());
    }



    @Test
    void capitalizeFirstLetter_NullOrEmptyString_Test() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("capitalizeFirstLetter", String.class);
        method.setAccessible(true);

        assertNull(method.invoke(customerService, (String) null));
        assertEquals("", method.invoke(customerService, ""));
    }

    @Test
    void capitalizeFirstLetter_ValidString_Test() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("capitalizeFirstLetter", String.class);
        method.setAccessible(true);

        assertEquals("John", method.invoke(customerService, "john"));
        assertEquals("John doe", method.invoke(customerService, "john doe"));
    }
}

