package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Orders;
import com.shopping.ecommerce.entity.OrderStatus;
import com.shopping.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusUpdater {

    private final EmailService emailService;
    private final OrderRepository orderRepository;
    private static final String SUBJECT = "Email Verification";
    private static final String BODY_TEMPLATE = "Thank you for signing up with our E-commerce website! Your order is %s.";

    @Autowired
    public OrderStatusUpdater(OrderRepository orderRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 6000)
    public void updateOrderStatus() {
        List<Orders> orders = orderRepository.findAll();
        LocalDateTime currentTime = LocalDateTime.now();

        for (Orders order : orders) {
            String email = order.getCustomer().getEmail();
            String body;
            switch (order.getOrderStatus()) {
                case PENDING:
                    if (currentTime.minusMinutes(10).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.SHIPPED);
                        body = String.format(BODY_TEMPLATE, "Shipped");
                        sendEmail(email, SUBJECT, body);
                    }
                    break;
                case SHIPPED:
                    if (currentTime.minusHours(1).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.OUT_OF_DELIVERY);
                        body = String.format(BODY_TEMPLATE, "Out for Delivery");
                        sendEmail(email, SUBJECT, body);
                    }
                    break;
                case OUT_OF_DELIVERY:
                    if (currentTime.minusHours(2).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.DELIVERED);
                        body = String.format(BODY_TEMPLATE, "Delivered");
                        sendEmail(email, SUBJECT, body);
                    }
                    break;
                case DELIVERED:
                    if (currentTime.minusHours(5).isAfter(order.getDateTime())) {
                        order.setOrderStatus(OrderStatus.DELIVERED);
                    }
                    break;
                default:
            }
        }

        orderRepository.saveAll(orders);
    }

    private void sendEmail(String email, String subject, String body) {
        emailService.sendEmail(email, subject, body);
    }
}
