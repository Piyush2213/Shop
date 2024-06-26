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
import java.util.stream.Collectors;

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

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private EmailService emailService;


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

        String subject = "Order Confirmation";
        String body = "Thank you for your order!\n\n" +
                "Order ID: " + createdOrder.getId() + "\n" +
                "Total Amount: " + createdOrder.getTotalAmount() + "\n" +
                "Delivery Address: " + savedAddress.getHouseNo() + ", " + savedAddress.getStreet() + ", " + savedAddress.getCity() + ", " + savedAddress.getPin() + "\n" +
                "Products:\n";

        for (OrderItemResponse orderItem : orderItemResponses) {
            body += orderItem.getProductName() + " - Quantity: " + orderItem.getQuantity() + ", Price: " + orderItem.getPrice() + "\n";
        }
        emailService.sendEmail(customer.getEmail(), subject, body);


        cartItemRepository.deleteByCart(cart);
        cartRepository.deleteByCustomerId(customer.getId());

        return new ServiceResponse<>(response, "Order created successfully.", HttpStatus.CREATED);
    }
    @Transactional
    public ServiceResponse<List<OrderResponse>> getAllOrders(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, "Invalid token or user not found.", HttpStatus.UNAUTHORIZED);
        }

        List<Orders> orders = orderRepository.findAllByCustomerId(customer.getId());
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> createOrderResponse(order, customer))
                .collect(Collectors.toList());

        return new ServiceResponse<>(orderResponses, "All orders retrieved successfully.", HttpStatus.OK);
    }

    private OrderResponse createOrderResponse(Orders order, Customer customer) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setName(customer.getName());
        response.setTotalAmount(order.getTotalAmount());
        response.setCustomerId(customer.getId());
        response.setDateTime(order.getDateTime());
        response.setOrderStatus(order.getOrderStatus());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setProductIds(order.getOrderItems().stream()
                .map(orderItem -> Long.valueOf(orderItem.getProductId()))
                .collect(Collectors.toList()));

        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setProductId(orderItem.getProductId());
                    itemResponse.setProductName(getProductName(orderItem.getProductId()));
                    itemResponse.setProductImageURL(orderItem.getImageURL());
                    itemResponse.setQuantity(orderItem.getQuantity());
                    itemResponse.setPrice(orderItem.getPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setOrderItems(orderItemResponses);
        return response;
    }


    @Transactional
    public ServiceResponse<String> cancelOrder(int orderId, HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, "Invalid token or user not found.", HttpStatus.UNAUTHORIZED);
        }

        // Check if the order belongs to the customer
        Orders order = orderRepository.findByIdAndCustomerId(Long.valueOf(orderId), customer.getId());
        if (order == null) {
            return new ServiceResponse<>(null, "Order not found or does not belong to the customer.", HttpStatus.NOT_FOUND);
        }

        order.setOrderStatus(OrderStatus.Cancelled);
        orderRepository.save(order);

        orderItemRepository.deleteByOrder(order);
        String subject = "Order Confirmation";
        String body = "Thank you for your shopping!\n\n";
        emailService.sendEmail(customer.getEmail(), subject, body);

        return new ServiceResponse<>(null,"Order cancelled successfully.", HttpStatus.OK);
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
