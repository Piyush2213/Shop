package com.shopping.ecommerce.adminServiceTest;

import com.shopping.ecommerce.entity.Admin;
import com.shopping.ecommerce.exception.ExistsException;
import com.shopping.ecommerce.repository.AdminRepository;
import com.shopping.ecommerce.request.AdminLoginRequest;
import com.shopping.ecommerce.response.AdminLoginResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.service.AdminService;
import java.lang.reflect.Method;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void adminLogin_ValidCredentials_Test() {
        AdminLoginRequest request = new AdminLoginRequest("admin@example.com", "password");
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setName("john");
        admin.setRole("admin");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(admin);

        ServiceResponse<AdminLoginResponse> response = adminService.adminLogin(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("John", response.getData().getName());
        assertEquals("admin", response.getData().getRole());
        assertEquals("LogIn Successful", response.getMessage());
    }

    @Test
    void adminLogin_InvalidCredentials_Test() {
        AdminLoginRequest request = new AdminLoginRequest("admin@example.com", "wrongpassword");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(null);

        ServiceResponse<AdminLoginResponse> response = adminService.adminLogin(request);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertNull(response.getData());
        assertEquals("Invalid email or password", response.getMessage());
    }

    @Test
    void logout_ValidToken_Test() {
        String token = "validToken";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        Admin admin = new Admin();
        admin.setId(1);
        admin.setToken(token);
        when(req.getHeader("Authorization")).thenReturn(token);
        when(adminRepository.findByToken(token)).thenReturn(admin);

        String result = adminService.logout(req, res);

        assertEquals("Logged Out", result);
        verify(adminRepository).updateToken(1, null);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(res).addCookie(cookieCaptor.capture());
        Cookie tokenCookie = cookieCaptor.getValue();
        assertEquals("token", tokenCookie.getName());
        assertNull(tokenCookie.getValue());
        assertEquals(0, tokenCookie.getMaxAge());
        assertEquals("/", tokenCookie.getPath());
    }

    @Test
    void logout_InvalidToken_Test() {
        String token = "invalidToken";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getHeader("Authorization")).thenReturn(token);
        when(adminRepository.findByToken(token)).thenReturn(null);

        assertThrows(ExistsException.class, () -> adminService.logout(req, res));
    }

    @Test
    void generateToken_ValidAdmin_Test() throws Exception {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setId(1);

        Method method = AdminService.class.getDeclaredMethod("generateToken", Admin.class);
        method.setAccessible(true);
        String token = (String) method.invoke(adminService, admin);

        assertNotNull(token);
        verify(adminRepository).updateToken(1, token);
    }

    @Test
    void capitalizeFirstLetter_NullOrEmptyString_Test() throws Exception {
        Method method = AdminService.class.getDeclaredMethod("capitalizeFirstLetter", String.class);
        method.setAccessible(true);

        assertNull(method.invoke(adminService, (String) null));
        assertEquals("", method.invoke(adminService, ""));
    }

    @Test
    void capitalizeFirstLetter_ValidString_Test() throws Exception {
        Method method = AdminService.class.getDeclaredMethod("capitalizeFirstLetter", String.class);
        method.setAccessible(true);

        assertEquals("John", method.invoke(adminService, "john"));
        assertEquals("John doe", method.invoke(adminService, "john doe"));
    }
}
