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
public class OrderItemResponse {
    private int productId;
    private String productName;
    private String productImageURL;
    private int quantity;
    private BigDecimal price;

}
