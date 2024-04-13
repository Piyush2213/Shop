package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.*;
import com.shopping.Ecommerce.repository.*;
import com.shopping.Ecommerce.response.OrderItemResponse;
import com.shopping.Ecommerce.response.OrderResponse;
import com.shopping.Ecommerce.response.ServiceResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;


    @Transactional
    public ServiceResponse<OrderResponse> createOrder(Address address, HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, "Invalid token or user not found.", HttpStatus.UNAUTHORIZED);
        }

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            return new ServiceResponse<>(null, "Cart is empty.", HttpStatus.BAD_REQUEST);
        }

        address.setCustomer(customer);

        Address savedAddress = addressRepository.save(address);

        Orders order = new Orders();
        order.setCustomer(customer);
        order.setDeliveryAddress(savedAddress);
        order.setTotalAmount(BigDecimal.ZERO);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Long> productIds = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setImageURL(cartItem.getProduct().getImageURL());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(cartItem.getTotalPrice());
            productIds.add((long) cartItem.getProduct().getId());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setDateTime(LocalDateTime.now());
        order.setDeliveryAddress(savedAddress);

        Orders createdOrder = orderRepository.save(order);


        OrderResponse response = new OrderResponse();
        response.setId(createdOrder.getId());
        response.setName(customer.getName());
        response.setTotalAmount(createdOrder.getTotalAmount());
        response.setCustomerId(customer.getId());
        response.setDateTime(createdOrder.getDateTime());
        response.setOrderStatus(OrderStatus.Pending);
        response.setDeliveryAddress(savedAddress);
        response.setProductIds(productIds);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : createdOrder.getOrderItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductId(orderItem.getProductId());
            itemResponse.setProductName(getProductName(orderItem.getProductId()));
            itemResponse.setProductImageURL(orderItem.getImageURL());
            itemResponse.setQuantity(orderItem.getQuantity());
            itemResponse.setPrice(orderItem.getPrice());
            orderItemResponses.add(itemResponse);
        }
        response.setOrderItems(orderItemResponses);

        cartItemRepository.deleteByCart(cart);
        cartRepository.deleteByCustomerId(customer.getId());

        return new ServiceResponse<>(response, "Order created successfully.", HttpStatus.CREATED);
    }




    private String getProductName(int productId) {
        Product product = productRepository.findById(productId).orElse(null);
        return product != null ? product.getName() : "Product Name";
    }

    private Customer getUserFromToken(String token) {
        Customer customer = customerRepository.findByToken(token);
        if (customer != null) {
            return customer;
        }
        return null;
    }
}
