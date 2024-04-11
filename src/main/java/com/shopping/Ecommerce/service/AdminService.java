package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Admin;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.repository.AdminRepository;
import com.shopping.Ecommerce.request.AdminLoginRequest;
import com.shopping.Ecommerce.response.AdminLoginResponse;
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
    @Autowired
    AdminRepository adminRepository;

    public ServiceResponse<AdminLoginResponse> adminLogin(AdminLoginRequest request){
        Admin admin = adminRepository.findByEmail(request.getEmail());
        if (admin == null || !admin.getPassword().equals(request.getPassword())) {
            return new ServiceResponse<>(null, "Invalid email or password", HttpStatus.BAD_REQUEST);
        }


        String token = generateToken(admin);

        String capitalizedFirstName = capitalizeFirstLetter(admin.getName());
        String role = admin.getRole();
        System.out.println("Geeting admin:" + role);
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
            throw new RuntimeException("Invalid token");
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
        String name = capitalizeFirstLetter(admin.getName());
        String token = Jwts.builder()

                .setSubject(admin.getEmail())
                .claim("name", admin.getName())
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
