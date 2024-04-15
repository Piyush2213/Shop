package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.entity.Address;
import com.shopping.Ecommerce.response.OrderResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.service.OrderService;
import com.shopping.Ecommerce.service.OrderStatusUpdater;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderStatusUpdater orderStatusUpdater;

    @PostMapping
    public ResponseEntity<ServiceResponse<OrderResponse>> createOrder(@RequestBody Address address, HttpServletRequest req) {
        ServiceResponse<OrderResponse> serviceResponse = orderService.createOrder(address, req);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @GetMapping
    public ResponseEntity<ServiceResponse<List<OrderResponse>>> getAllOrders(HttpServletRequest req){
        ServiceResponse<List<OrderResponse>> serviceResponse = orderService.getAllOrders(req);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ServiceResponse<String>> cancelOrder(@PathVariable("orderId") int orderId,HttpServletRequest req){
        ServiceResponse<String> serviceResponse = orderService.cancelOrder(orderId,req);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }



    @PostMapping("/update-status")
    public ResponseEntity<String> updateOrderStatus() {
        orderStatusUpdater.updateOrderStatus();
        return ResponseEntity.ok("Order statuses updated successfully.");
    }
}
