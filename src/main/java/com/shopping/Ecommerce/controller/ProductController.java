package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.request.ProductRequest;
import com.shopping.Ecommerce.response.ProductResponse;
import com.shopping.Ecommerce.response.ProductsResponse;
import com.shopping.Ecommerce.service.ProductService;
import com.shopping.Ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<ProductsResponse>> getAllProducts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "perPage", defaultValue = "26") int perPage) {

        List<ProductsResponse> responseList = productService.getAllProducts(page, perPage);

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<ProductResponse>> getProduct(@PathVariable Integer id){
        ServiceResponse<ProductResponse> response = productService.getProduct(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/add")
    public ResponseEntity<ServiceResponse<ProductResponse>> addProduct(@RequestBody ProductRequest productRequest, HttpServletRequest req ) {
        if ( authService.isAdmin(req)) {
            ServiceResponse<ProductResponse> response = productService.addProduct(productRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            ServiceResponse<ProductResponse> errorResponse = new ServiceResponse<>(null, "Invalid token or not authorized", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<ProductResponse>> updateProduct(HttpServletRequest req, @PathVariable Integer id, @RequestBody ProductRequest productRequest) {
        if (authService.isAdmin(req)) {
            ServiceResponse<ProductResponse> response = productService.updateProduct(id, productRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            ServiceResponse<ProductResponse> errorResponse = new ServiceResponse<>(null, "Invalid token or not authorized", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<String>> deleteProduct(@PathVariable Integer id, HttpServletRequest req) {
        if (authService.isAdmin(req)){
            ServiceResponse<String> response = productService.deleteProduct(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        }else{
            ServiceResponse<String> errorResponse = new ServiceResponse<>(null, "Invalid token or not authorized", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }





}
