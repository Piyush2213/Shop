package com.shopping.ecommerce.controller;

import com.shopping.ecommerce.entity.ProductES;
import com.shopping.ecommerce.exception.ExistsException;
import com.shopping.ecommerce.repository.ElasticsearchProductSearchRepository;
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

    private final ElasticsearchProductSearchRepository productESRepository;
    private final Random random;

    @Autowired
    public DataInitializerES(ElasticsearchProductSearchRepository productESRepository) {
        this.productESRepository = productESRepository;
        this.random = new Random();
    }

    private BigDecimal getRandomPrice() {
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
                String header = br.readLine();
                if (header != null) {
                    String line;
                    List<ProductES> products = new ArrayList<>();
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
                }
            } catch (IOException e) {
                throw new ExistsException("Error reading CSV file");
            }
        }
    }



}
