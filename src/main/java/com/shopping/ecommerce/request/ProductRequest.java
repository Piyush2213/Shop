package com.shopping.ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String imageURL;
    private String subCategory;
    private String category;
    private String colour;
    private String gender;
    private String pUsage;
    private Integer quantity;
    private BigDecimal price;
}
