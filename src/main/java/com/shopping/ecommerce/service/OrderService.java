package com.shopping.ecommerce.service;

import com.razorpay.*;
import com.razorpay.RazorpayClient;

import com.shopping.ecommerce.entity.*;
import com.shopping.ecommerce.entity.Customer;

import com.shopping.ecommerce.repository.*;
import com.shopping.ecommerce.response.OrderItemResponse;
import com.shopping.ecommerce.response.OrderResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class OrderService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(CustomerRepository customerRepository, OrderRepository orderRepository, CartRepository cartRepository,
                        CartItemRepository cartItemRepository, AddressRepository addressRepository, ProductRepository productRepository
                         , EmailService emailService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String INVALID_TOKEN = "Invalid token or user not found.";

    @Transactional
    public ServiceResponse<OrderResponse> createOrder(Address address, HttpServletRequest req) throws RazorpayException {
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            return new ServiceResponse<>(null, "Cart is empty.", HttpStatus.NOT_FOUND);
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
        order.setOrderStatus(OrderStatus.PENDING);

        Orders createdOrder = orderRepository.save(order);

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",order.getTotalAmount().multiply(new BigDecimal(100)).intValue());
        orderRequest.put("currency","INR");
        orderRequest.put("receipt", generateUniqueReceiptNumber());



        Order razorPayOrderDetails = razorpay.orders.create(orderRequest);

        String razorPayStatus = razorPayOrderDetails.get("status");
        BigDecimal razorpayAmount = order.getTotalAmount();
        String razorpayCurrency = "INR";
        String razorpayReceipt = generateUniqueReceiptNumber();

        try {
            createdOrder.setRazorPayOrderId(razorPayOrderDetails.get("id"));
            createdOrder.setOrderStatus(OrderStatus.valueOf(razorPayStatus.toUpperCase()));
            createdOrder.setRazorpayAmount(razorpayAmount);
            createdOrder.setRazorpayCurrency(razorpayCurrency);
            createdOrder.setRazorpayReceipt(razorpayReceipt);
        } catch (IllegalArgumentException e) {
            createdOrder.setOrderStatus(OrderStatus.PENDING);
        }
        System.out.println("Created order: "+ createdOrder);
        orderRepository.save(createdOrder);



        OrderResponse response = new OrderResponse();
        response.setId(createdOrder.getId());
        response.setName(customer.getName());
        response.setTotalAmount(createdOrder.getTotalAmount());
        response.setCustomerId(customer.getId());
        response.setDateTime(createdOrder.getDateTime());
        response.setOrderStatus(createdOrder.getOrderStatus());
        response.setDeliveryAddress(savedAddress);
        response.setProductIds(productIds);
        response.setRazorPayOrderId(createdOrder.getRazorPayOrderId());


        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : createdOrder.getOrderItems()) {
            OrderItemResponse itemResponse = modelMapper.map(orderItem ,OrderItemResponse.class);
            itemResponse.setProductName(getProductName(orderItem.getProductId()));
            orderItemResponses.add(itemResponse);
        }
        response.setOrderItems(orderItemResponses);


        String subject = "Order Confirmation";
        String body = "Thank you for your order!\n\n" +
                "Order ID: " + createdOrder.getRazorPayOrderId() + "\n" +
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
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        List<Orders> orders = orderRepository.findAllByCustomerId(customer.getId());
        System.out.println("These are all orders" + orders);
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Orders order : orders) {
            List<OrderItemResponse> orderedProductsList = new ArrayList<>();
            for (OrderItem orderItem : order.getOrderItems()) {

                OrderItemResponse orderedProduct = new OrderItemResponse();
                orderedProduct.setProductId(orderItem.getProductId());
                orderedProduct.setQuantity(orderItem.getQuantity());
                Product product = productRepository.findById(orderItem.getProductId()).orElse(null);
                if (product != null) {
                    orderedProduct.setProductId(product.getId());
                    orderedProduct.setProductName(product.getName());
                    orderedProduct.setProductImageURL(product.getImageURL());
                    orderedProduct.setPrice(product.getPrice());
                }
                orderedProductsList.add(orderedProduct);
            }

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setTotalAmount(order.getTotalAmount());
            orderResponse.setCustomerId(order.getCustomer().getId());
            orderResponse.setOrderItems(orderedProductsList);
            orderResponse.setDateTime(order.getDateTime());
            System.out.println("Order Status in get order is: " + order.getOrderStatus());
            orderResponse.setOrderStatus(order.getOrderStatus());
            orderResponse.setDeliveryAddress(order.getDeliveryAddress());

            orderResponses.add(orderResponse);
        }

        return new ServiceResponse<>(orderResponses, "All orders retrieved successfully.", HttpStatus.OK);


    }







    @Transactional
    public ServiceResponse<String> cancelOrder(int orderId, HttpServletRequest req) throws RazorpayException {
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        Orders order = orderRepository.findByIdAndCustomerId(Long.valueOf(orderId), customer.getId());
        if (order == null) {
            return new ServiceResponse<>(null, "Order not found or does not belong to the customer.", HttpStatus.NOT_FOUND);
        }

        System.out.println("Order status: " + order.getOrderStatus());

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        System.out.println("Order status after updating: " + order.getOrderStatus());

        String subject = "Order Cancellation";
        String body = "Thank you for your shopping!. You will get your refund within 4-5 business days.\n\n";
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
    private static String generateUniqueReceiptNumber() {
        String input = System.currentTimeMillis() + UUID.randomUUID().toString();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 40);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating receipt number", e);
        }
    }










}
