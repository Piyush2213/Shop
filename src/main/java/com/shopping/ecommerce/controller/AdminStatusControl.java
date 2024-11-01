package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.entity.OrderStatus;
import com.shopping.ecommerce.entity.Orders;
import com.shopping.ecommerce.service.AdminOrderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = {"*"}, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class AdminStatusControl {
    private final AdminOrderService orderService;

    @Autowired
    public AdminStatusControl(AdminOrderService orderService){
        this.orderService = orderService;
    }


    private boolean isAdmin(String token) {
        try {
            byte[] keyBytes = "adbashdgasgdahjshdjagsdgjhasdjahgdhasghjdgahjsgdhgsfvgavjvfgavytsdgavsdyasfgavgfvatyvdagbsgvfatyad".getBytes();
            System.out.println("key bytes: " + keyBytes);
            Claims claims = Jwts.parser()
                    .setSigningKey(keyBytes)
                    .parseClaimsJws(token)
                    .getBody();

            String role = (String) claims.get("role");
            return "admin".equals(role);
        } catch (JwtException e) {
            System.err.println("Token Verification Failed: " + e.getMessage());
            return false;
        }
    }


    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Integer id, @RequestParam OrderStatus orderStatus, HttpServletRequest req) {
        String token = req.getHeader("Authorization");

        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Admin privileges required.");
        }

        orderService.updateOrderStatus(id, orderStatus);
        return ResponseEntity.ok("Status updated successfully");
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Orders>> getAllOrders(HttpServletRequest req) {
        try {
            String token = req.getHeader("Authorization");

            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            List<Orders> allOrders = orderService.getAllOrdersAdmin();
            return ResponseEntity.ok(allOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}
