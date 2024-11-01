package com.shopping.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemCreateResponse {
    private int cartId;
    private String productName;
    private int quantity;
    private BigDecimal amount;
    private int productId;
}
