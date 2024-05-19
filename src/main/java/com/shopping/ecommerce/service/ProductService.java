package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Product;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.repository.ProductRepository;
import com.shopping.ecommerce.request.ProductRequest;
import com.shopping.ecommerce.response.ProductResponse;
import com.shopping.ecommerce.response.ProductsResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public ProductService(ProductRepository productRepository, ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProductsResponse> getAllProducts(int page, int perPage, String sort, List<String> subCategory) {
        Pageable pageable;


        if ("desc".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page - 1, perPage, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page - 1, perPage, Sort.by("price").ascending());
        }

        Page<Product> productPage;

        if (subCategory != null && !subCategory.isEmpty()) {
            productPage = productRepository.findBySubCategoryIn(subCategory, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<Product> products = productPage.getContent();
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    private ProductsResponse convertToDto(Product product) {
        return modelMapper.map(product, ProductsResponse.class);
    }


    public ServiceResponse<ProductResponse> getProduct(Integer id){
        //optional does not return null even if product not found, it will return empty object.
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ServiceResponse<> (null, "Product Not Found", HttpStatus.NOT_FOUND);
        }

        Product product = optionalProduct.get();
        ProductResponse response = modelMapper.map(product, ProductResponse.class);

        return new ServiceResponse<>(response, "Product Found", HttpStatus.OK);
    }


    public ServiceResponse<ProductResponse> addProduct(ProductRequest request){
        if (request.getName() == null || request.getPrice() == null) {
            return new ServiceResponse<>(null, "Name and Price are required fields", HttpStatus.BAD_REQUEST);
        }
        Product newProduct = modelMapper.map(request, Product.class);
        Product savedProduct = productRepository.save(newProduct);
        ProductResponse response = modelMapper.map(savedProduct, ProductResponse.class);

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

        ProductResponse response = modelMapper.map(updatedProduct, ProductResponse.class);


        return new ServiceResponse<>(response, "Updated Successfully", HttpStatus.OK);
    }





    public ServiceResponse<String> deleteProduct(Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ServiceResponse<>(null,"Product Not Found", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        return new ServiceResponse<>(null,"Product Deleted!", HttpStatus.OK);
    }

    public ServiceResponse<Long> productCount(){
        long count = productRepository.count();
        return new ServiceResponse<>(count, "All Product Counted", HttpStatus.OK);
    }

}
