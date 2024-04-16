package com.shopping.Ecommerce.controller;

import com.shopping.Ecommerce.entity.ProductES;
import com.shopping.Ecommerce.repository.ElasticsearchProductSearchRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializerES {

    @Autowired
    private ElasticsearchProductSearchRepository productESRepository;

    private BigDecimal getRandomPrice() {
        Random random = new Random();
        int minPrice = 100;
        int maxPrice = 2000;
        int randomPrice = random.nextInt(maxPrice - minPrice + 1) + minPrice;
        return BigDecimal.valueOf(randomPrice);
    }

    @PostConstruct
    public void initializeData() {
        if (productESRepository.count() == 0) {
            try (
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("fashion.csv");
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
                ) {
                // ProductId,Gender,Category,SubCategory,ProductType,Colour,Usage,ProductTitle,Image,ImageURL
                // price, description, quantity
                String line;
                List<ProductES> products = new ArrayList<>();
                br.readLine();
                int id = 1;
                while ((line = br.readLine()) != null) {
                    List<String> values = Arrays.asList(line.split(","));
                    ProductES product = new ProductES(
                            id++,
                            values.get(7), // productTitle
                            getRandomPrice(),
                            values.get(9)  // imageURL
                    );
                    products.add(product);
                }
                productESRepository.saveAll(products);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}