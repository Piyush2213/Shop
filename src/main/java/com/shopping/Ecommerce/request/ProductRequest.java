package com.shopping.Ecommerce.request;

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
    private int id;
    private String name;
    private String imageURL;
    private String subCategory;
    private String category;
    private String colour;
    private String gender;
    private String pUsage;
    private Integer Quantity;
    private BigDecimal price;
}
