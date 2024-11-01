package com.shopping.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemListResponse {
    private List<CartItemResponse> cartItems;
    private BigDecimal totalAmount;
}
