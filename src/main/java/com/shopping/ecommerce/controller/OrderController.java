package com.shopping.ecommerce.controller;

import com.razorpay.RazorpayException;
import com.shopping.ecommerce.entity.Address;
import com.shopping.ecommerce.response.OrderResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.service.OrderService;
import com.shopping.ecommerce.service.OrderStatusUpdater;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})

public class OrderController {
    private final OrderService orderService;
    private final OrderStatusUpdater orderStatusUpdater;

    @Autowired
    public OrderController(OrderService orderService, OrderStatusUpdater orderStatusUpdater) {
        this.orderService = orderService;
        this.orderStatusUpdater = orderStatusUpdater;
    }

    @PostMapping
    public ResponseEntity<ServiceResponse<OrderResponse>> createOrder(@RequestBody Address address, HttpServletRequest req) throws RazorpayException {
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
