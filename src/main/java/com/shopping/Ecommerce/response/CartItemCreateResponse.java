package com.shopping.Ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

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
