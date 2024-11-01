package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.entity.CartItem;
import com.shopping.ecommerce.response.*;
import com.shopping.ecommerce.request.CartRequest;
import com.shopping.ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = {"*"}, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class Cart {

    private final CartService cartService;

    @Autowired
    public Cart(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cartItems")
    public ResponseEntity<ServiceResponse<CartItemCreateResponse>> createCartItem(HttpServletRequest req, @RequestBody CartRequest request) {
        ServiceResponse<CartItemCreateResponse> serviceResponse = cartService.createCartItem(req, request);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @GetMapping("/cartItems")
    public ResponseEntity<ServiceResponse<CartItemListResponse>> getAllCartItems(HttpServletRequest req){
        ServiceResponse<CartItemListResponse> serviceResponse = cartService.getAllCartItems(req);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @GetMapping("/cartItems/{itemId}")
    public ResponseEntity<ServiceResponse<CartItemResponse>> getCartItems(HttpServletRequest req, @PathVariable("itemId") Integer itemId){
        ServiceResponse<CartItemResponse> serviceResponse = cartService.getCartItems(req, itemId);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @PutMapping("/cartItems/{itemId}")
    public ResponseEntity<ServiceResponse<CartItemResponse>> updateCartItem(HttpServletRequest req, @PathVariable("itemId") Integer itemId, @RequestBody CartItem updatedCartItem){
        ServiceResponse<CartItemResponse> serviceResponse = cartService.updateCartItem(req, itemId, updatedCartItem);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @DeleteMapping("/cartItems/{itemId}")
    public ResponseEntity<ServiceResponse<String>> deleteCartItem(HttpServletRequest req, @PathVariable("itemId") Integer itemId){
        ServiceResponse<String> serviceResponse = cartService.deleteCartItem(req, itemId);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }
}
