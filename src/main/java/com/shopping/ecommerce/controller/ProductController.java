package com.shopping.ecommerce.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.shopping.ecommerce.entity.ProductES;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.request.ProductRequest;
import com.shopping.ecommerce.response.ProductResponse;
import com.shopping.ecommerce.response.ProductsResponse;
import com.shopping.ecommerce.service.ElasticSearchService;
import com.shopping.ecommerce.service.ProductService;
import com.shopping.ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final AuthService authService;
    private final ElasticSearchService elasticSearchService;

    @Autowired
    public ProductController(ProductService productService, AuthService authService, ElasticSearchService elasticSearchService) {
        this.productService = productService;
        this.authService = authService;
        this.elasticSearchService = elasticSearchService;
    }
    private static final String UNAUTHORIZED_MESSAGE = "Invalid token or not authorized";

    @GetMapping("/fuzzySearch/{approximateProductName}")
    List<ProductES> fuzzySearch(@PathVariable String approximateProductName) throws IOException {
        SearchResponse<ProductES> searchResponse = elasticSearchService.fuzzySearch(approximateProductName);
        List<Hit<ProductES>> hitList = searchResponse.hits().hits();
        List<ProductES> productList = new ArrayList<>();
        for (Hit<ProductES> hit : hitList) {
            productList.add(hit.source());
        }
        return productList;
    }

    @GetMapping
    public ResponseEntity<List<ProductsResponse>> getAllProducts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "perPage", defaultValue = "26") int perPage) {
        List<ProductsResponse> responseList = productService.getAllProducts(page, perPage);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<ProductResponse>> getProduct(@PathVariable Integer id) {
        ServiceResponse<ProductResponse> response = productService.getProduct(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ServiceResponse<ProductResponse>> addProduct(@RequestBody ProductRequest productRequest, HttpServletRequest req) {
        if (authService.isAdmin(req)) {
            ServiceResponse<ProductResponse> response = productService.addProduct(productRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            ServiceResponse<ProductResponse> errorResponse = new ServiceResponse<>(null, UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<ProductResponse>> updateProduct(HttpServletRequest req, @PathVariable Integer id, @RequestBody ProductRequest productRequest) {
        if (authService.isAdmin(req)) {
            ServiceResponse<ProductResponse> response = productService.updateProduct(id, productRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            ServiceResponse<ProductResponse> errorResponse = new ServiceResponse<>(null, UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<String>> deleteProduct(@PathVariable Integer id, HttpServletRequest req) {
        if (authService.isAdmin(req)) {
            ServiceResponse<String> response = productService.deleteProduct(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } else {
            ServiceResponse<String> errorResponse = new ServiceResponse<>(null, UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
