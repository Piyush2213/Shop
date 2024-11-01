package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Admin;
import com.shopping.ecommerce.exception.ExistsException;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.repository.AdminRepository;
import com.shopping.ecommerce.request.AdminLoginRequest;
import com.shopping.ecommerce.response.AdminLoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public ServiceResponse<AdminLoginResponse> adminLogin(AdminLoginRequest request){
        Admin admin = adminRepository.findByEmail(request.getEmail());
        if (admin == null || !admin.getPassword().equals(request.getPassword())) {
            return new ServiceResponse<>(null, "Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        String token = generateToken(admin);

        String capitalizedFirstName = capitalizeFirstLetter(admin.getName());
        String role = admin.getRole();

        AdminLoginResponse response = new AdminLoginResponse(token, capitalizedFirstName, role, "Login Successful");

        return new ServiceResponse<>(response, "LogIn Successful", HttpStatus.OK);
    }

    public String logout(HttpServletRequest req, HttpServletResponse res) {
        String token = req.getHeader("Authorization");
        Admin admin = getAdminFromToken(token);
        if (admin != null) {
            adminRepository.updateToken(admin.getId(), null);
            Cookie tokenCookie = new Cookie("token", null);
            tokenCookie.setMaxAge(0);
            tokenCookie.setPath("/");
            res.addCookie(tokenCookie);
            return "Logged Out";
        } else {
            throw new ExistsException("Invalid token");
        }
    }

    private Admin getAdminFromToken(String token) {
        Admin admin = adminRepository.findByToken(token);
        if (admin != null) {
            return admin;
        }
        throw new ExistsException("Invalid token or user not found.");
    }

    private String generateToken(Admin admin) {
        byte[] keyBytes = "adbashdgasgdahjshdjagsdgjhasdjahgdhasghjdgahjsgdhgsfvgavjvfgavytsdgavsdyasfgavgfvatyvdagbsgvfatyad".getBytes();
        String token = Jwts.builder()
                .setSubject(admin.getEmail())
                .claim("role", "admin")
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();

        admin.setToken(token);
        adminRepository.updateToken(admin.getId(), token);

        return token;
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
