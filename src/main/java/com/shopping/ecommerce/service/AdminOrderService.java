package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.OrderStatus;
import com.shopping.ecommerce.entity.Orders;
import com.shopping.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public AdminOrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }


    public void updateOrderStatus(Integer id, OrderStatus orderStatus){
        Orders updateOrder = orderRepository.findById(id).orElse(null);
        updateOrder.setOrderStatus(orderStatus);
        orderRepository.save(updateOrder);
    }
    public List<Orders> getAllOrdersAdmin() {
        return orderRepository.findAllOrders();
    }
}
