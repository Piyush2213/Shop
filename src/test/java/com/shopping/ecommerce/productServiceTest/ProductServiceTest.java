package com.shopping.ecommerce.productServiceTest;


import com.shopping.ecommerce.entity.Product;
import com.shopping.ecommerce.repository.ProductRepository;
import com.shopping.ecommerce.request.ProductRequest;
import com.shopping.ecommerce.response.ProductResponse;
import com.shopping.ecommerce.response.ProductsResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_NoSubCategory_SortedAscending() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductsResponse.class))).thenReturn(new ProductsResponse());

        List<ProductsResponse> response = productService.getAllProducts(1, 10, "asc", null);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllProducts_WithSubCategory_SortedDescending() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findBySubCategoryIn(anyList(), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductsResponse.class))).thenReturn(new ProductsResponse());

        List<ProductsResponse> response = productService.getAllProducts(1, 10, "desc", Arrays.asList("sub1", "sub2"));

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(productRepository).findBySubCategoryIn(anyList(), any(Pageable.class));
    }

    @Test
    void getProduct_ProductFound() {
        Product product = new Product();
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(new ProductResponse());

        ServiceResponse<ProductResponse> response = productService.getProduct(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("Product Found", response.getMessage());
    }

    @Test
    void getProduct_ProductNotFound() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        ServiceResponse<ProductResponse> response = productService.getProduct(1);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertEquals("Product Not Found", response.getMessage());
    }

    @Test
    void addProduct_ValidRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("product");
        request.setPrice(BigDecimal.valueOf(100));

        Product product = new Product();
        Product savedProduct = new Product();
        when(modelMapper.map(any(ProductRequest.class), eq(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(new ProductResponse());

        ServiceResponse<ProductResponse> response = productService.addProduct(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("Added Successfully", response.getMessage());
    }

    @Test
    void addProduct_InvalidRequest() {
        ProductRequest request = new ProductRequest();

        ServiceResponse<ProductResponse> response = productService.addProduct(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertNull(response.getData());
        assertEquals("Name and Price are required fields", response.getMessage());
    }

    @Test
    void updateProduct_ProductFound() {
        ProductRequest request = new ProductRequest();
        request.setName("updatedProduct");
        request.setPrice(BigDecimal.valueOf(150.0));

        Product product = new Product();
        Product updatedProduct = new Product();
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(new ProductResponse());

        ServiceResponse<ProductResponse> response = productService.updateProduct(1, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("Updated Successfully", response.getMessage());
    }

    @Test
    void updateProduct_ProductNotFound() {
        ProductRequest request = new ProductRequest();

        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        ServiceResponse<ProductResponse> response = productService.updateProduct(1, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void deleteProduct_ProductFound() {
        Product product = new Product();
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

        ServiceResponse<String> response = productService.deleteProduct(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Product Deleted!", response.getMessage());
        verify(productRepository).deleteById(anyInt());
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        ServiceResponse<String> response = productService.deleteProduct(1);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Product Not Found", response.getMessage());
        verify(productRepository, never()).deleteById(anyInt());
    }

    @Test
    void productCount() {
        when(productRepository.count()).thenReturn(100L);

        ServiceResponse<Long> response = productService.productCount();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(100L, response.getData());
        assertEquals("All Product Counted", response.getMessage());
    }
}

