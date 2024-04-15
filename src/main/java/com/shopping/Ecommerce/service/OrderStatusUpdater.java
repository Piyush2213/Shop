package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Orders;
import com.shopping.Ecommerce.entity.OrderStatus;
import com.shopping.Ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class OrderStatusUpdater {

    @Autowired
    EmailService emailService;
    private final OrderRepository orderRepository;

    public OrderStatusUpdater(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedRate = 6000)
    public void updateOrderStatus() {
        List<Orders> orders = orderRepository.findAll();
        LocalDateTime currentTime = LocalDateTime.now();

        for (Orders order : orders) {
            switch (order.getOrderStatus()) {
                case Pending:
                    if (currentTime.minusMinutes(10).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.Shipped);
                    }
                    break;
                case Shipped:
                    if (currentTime.minusHours(1).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.Out_Of_Delivery);
                    }
                    break;
                case Out_Of_Delivery:
                    if (currentTime.minusHours(2).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.Delivered);
                    }
                    break;
                case Delivered:
                    if (currentTime.minusHours(5).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.Delivered);
                    }
                    break;
                default:
            }
        }

        orderRepository.saveAll(orders);

    }
}
