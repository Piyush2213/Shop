package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.service.ProductInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductInitializer {

    @Autowired
    private ProductInitializerService productInitializerService;
    @Autowired
    public void init() {
        productInitializerService.initializeData();
    }
}
