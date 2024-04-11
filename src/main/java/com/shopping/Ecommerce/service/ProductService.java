package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Product;
import com.shopping.Ecommerce.response.ServiceResponse;
import com.shopping.Ecommerce.repository.ProductRepository;
import com.shopping.Ecommerce.request.ProductRequest;
import com.shopping.Ecommerce.response.ProductResponse;
import com.shopping.Ecommerce.response.ProductsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductsResponse> getAllProducts(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<Product> products = productPage.getContent();
        List<ProductsResponse> responseList = new ArrayList<>();

        for (Product product : products) {
            ProductsResponse response = new ProductsResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setPrice(product.getPrice());
            response.setImageURL(product.getImageURL());
            responseList.add(response);
        }

        return responseList;
    }


    public ServiceResponse<ProductResponse> getProduct(Integer id){
        //optional does not return null even if product not found, it will return empty object.
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ServiceResponse<> (null, "Product Not Found", HttpStatus.NOT_FOUND);
        }

        Product product = optionalProduct.get();

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setImageURL(product.getImageURL());
        response.setColour(product.getColour());
        response.setSubCategory(product.getSubCategory());
        response.setCategory(product.getCategory());
        response.setGender(product.getGender());

        return new ServiceResponse<>(response, "Product Found", HttpStatus.OK);
    }


    public ServiceResponse<ProductResponse> addProduct(ProductRequest request){
        if (request.getName() == null || request.getPrice() == null) {
            return new ServiceResponse<>(null, "Name and Price are required fields", HttpStatus.BAD_REQUEST);
        }
        Product newProduct = new Product();

        newProduct.setName(request.getName());
        newProduct.setCategory(request.getCategory());
        newProduct.setGender(request.getGender());
        newProduct.setColour(request.getColour());
        newProduct.setQuantity(request.getQuantity());
        newProduct.setPrice(request.getPrice());
        newProduct.setImageURL(request.getImageURL());
        newProduct.setSubCategory(request.getSubCategory());

        Product savedProduct = productRepository.save(newProduct);

        ProductResponse response = new ProductResponse();

        response.setName(savedProduct.getName());
        response.setCategory(savedProduct.getCategory());
        response.setGender(savedProduct.getGender());
        response.setColour(savedProduct.getColour());
        response.setQuantity(savedProduct.getQuantity());
        response.setPrice(savedProduct.getPrice());
        response.setImageURL(savedProduct.getImageURL());
        response.setSubCategory(savedProduct.getSubCategory());

        return new ServiceResponse<>(response, "Added Successfully", HttpStatus.OK);
    }



    public ServiceResponse<ProductResponse> updateProduct(Integer id, ProductRequest request){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ServiceResponse<>(null, "Product not found", HttpStatus.NOT_FOUND);
        }

        Product existingProduct = optionalProduct.get();
        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }
        if (request.getCategory() != null) {
            existingProduct.setCategory(request.getCategory());
        }
        if (request.getGender() != null) {
            existingProduct.setGender(request.getGender());
        }
        if (request.getColour() != null) {
            existingProduct.setColour(request.getColour());
        }
        if (request.getQuantity() != null) {
            existingProduct.setQuantity(request.getQuantity());
        }
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
        }
        if (request.getImageURL() != null) {
            existingProduct.setImageURL(request.getImageURL());
        }
        if (request.getSubCategory() != null) {
            existingProduct.setSubCategory(request.getSubCategory());
        }

        Product updatedProduct = productRepository.save(existingProduct);

        ProductResponse response = new ProductResponse();

        response.setId(updatedProduct.getId());
        response.setName(updatedProduct.getName());
        response.setCategory(updatedProduct.getCategory());
        response.setGender(updatedProduct.getGender());
        response.setColour(updatedProduct.getColour());
        response.setQuantity(updatedProduct.getQuantity());
        response.setPrice(updatedProduct.getPrice());
        response.setImageURL(updatedProduct.getImageURL());
        response.setSubCategory(updatedProduct.getSubCategory());


        return new ServiceResponse<>(response, "Updated Successfully", HttpStatus.OK);
    }





    public ServiceResponse<String> deleteProduct(Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ServiceResponse<>(null,"Product Not Found", HttpStatus.BAD_REQUEST);
        }
        productRepository.deleteById(id);
        return new ServiceResponse<>(null,"Product Deleted!", HttpStatus.BAD_REQUEST);
    }

}
