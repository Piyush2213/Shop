package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.service.ProductInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductInitializer {

    private final ProductInitializerService productInitializerService;

    @Autowired
    public ProductInitializer(ProductInitializerService productInitializerService) {
        this.productInitializerService = productInitializerService;
    }

    @Autowired
    public void init() {
        productInitializerService.initializeData();
    }
}
