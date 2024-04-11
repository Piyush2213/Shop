package com.shopping.Ecommerce.controller;
import com.shopping.Ecommerce.response.CartItemListResponse;
import com.shopping.Ecommerce.response.CartItemResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.request.CartRequest;
import com.shopping.Ecommerce.response.CartItemCreateResponse;
import com.shopping.Ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = {"*"}, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
public class Cart {
    @Autowired
    private CartService cartService;
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

}
