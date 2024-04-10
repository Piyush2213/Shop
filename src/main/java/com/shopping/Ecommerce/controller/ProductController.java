package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.exception.ErrorResponse;
import com.shopping.Ecommerce.exception.ExistsException;
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
    public ResponseEntity<?> getProduct(@PathVariable Integer id){
        try {
            ProductResponse response = productService.getProduct(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExistsException ex) {
            ExistsException errorResponse = new ExistsException("Product not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest, HttpServletRequest req ) {
        if ( authService.isAdmin(req)) {
            ProductResponse response = productService.addProduct(productRequest);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(HttpServletRequest req, @PathVariable Integer id, @RequestBody ProductRequest productRequest) {
        if (authService.isAdmin(req)) {
            ProductResponse response = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token or not authorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        try {
            String message = productService.deleteProduct(id);
            return ResponseEntity.ok(message);
        } catch (ExistsException ex) {
            String message = ex.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }





}
