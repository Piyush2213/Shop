package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.entity.Address;
import com.shopping.Ecommerce.response.OrderResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @PostMapping
    public ResponseEntity<ServiceResponse<OrderResponse>> createOrder(@RequestBody Address address, HttpServletRequest req) {
        ServiceResponse<OrderResponse> serviceResponse = orderService.createOrder(address, req);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }
}
