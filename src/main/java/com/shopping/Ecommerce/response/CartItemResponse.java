package com.shopping.Ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private int id;
    private String productName;
    private String imageURL;

    private int quantity;
    private BigDecimal amount;
    private int productId;

}
