package com.shopping.Ecommerce.response;

import com.shopping.Ecommerce.entity.Address;
import com.shopping.Ecommerce.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private int id;
    private String name;
    private BigDecimal totalAmount;
    private int customerId;
    private List<OrderItemResponse> orderItems;
    private LocalDateTime dateTime;
    private List<Long> productIds;
    private Address deliveryAddress;
    private OrderStatus orderStatus;
}
