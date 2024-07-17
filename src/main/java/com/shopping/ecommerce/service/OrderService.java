package com.shopping.ecommerce.service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shopping.ecommerce.entity.*;
import com.shopping.ecommerce.exception.ExistsException;
import com.shopping.ecommerce.exception.OrderException;
import com.shopping.ecommerce.repository.*;
import com.shopping.ecommerce.response.ApiResponse;
import com.shopping.ecommerce.response.OrderItemResponse;
import com.shopping.ecommerce.response.OrderResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(CustomerRepository customerRepository, OrderRepository orderRepository, CartRepository cartRepository,
                        CartItemRepository cartItemRepository, AddressRepository addressRepository, ProductRepository productRepository,
                        OrderItemRepository orderItemRepository, EmailService emailService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
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

        Orders createdOrder = orderRepository.save(order);

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", order.getTotalAmount().multiply(new BigDecimal(100)).intValue()); // Amount in paise (INR currency)

        JSONObject customerJson = new JSONObject();
        customerJson.put("name", order.getCustomer().getName());
        customerJson.put("email", order.getCustomer().getEmail());
        paymentLinkRequest.put("customer", customerJson);

        JSONObject notify = new JSONObject();
        notify.put("email", true);
        notify.put("sms", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("callback_url", "http://localhost:5173/orders");
        paymentLinkRequest.put("callback_method", "get");
        System.out.println("Payment Link Request: " + paymentLinkRequest.toString());

        PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

        String paymentLinkId = payment.get("id");
        String paymentLinkUrl = payment.get("short_url");
        System.out.println("Payment Link URL: " + paymentLinkUrl);
        System.out.println("Payment Link Id: " + paymentLinkId);




        OrderResponse response = new OrderResponse();
        response.setId(createdOrder.getId());
        response.setName(customer.getName());
        response.setTotalAmount(createdOrder.getTotalAmount());
        response.setCustomerId(customer.getId());
        response.setDateTime(createdOrder.getDateTime());
        response.setOrderStatus(OrderStatus.PENDING);
        response.setDeliveryAddress(savedAddress);
        response.setProductIds(productIds);
        response.setPaymentLinkUrl(paymentLinkUrl);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : createdOrder.getOrderItems()) {
            OrderItemResponse itemResponse = modelMapper.map(orderItem ,OrderItemResponse.class);
            itemResponse.setProductName(getProductName(orderItem.getProductId()));
            orderItemResponses.add(itemResponse);
        }
        response.setOrderItems(orderItemResponses);

        ServiceResponse<Payment> getPaymentDetails = getPaymentDetails(paymentLinkId);
        System.out.println("Response of redirect"+ getPaymentDetails);

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
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        List<Orders> orders = orderRepository.findAllByCustomerId(customer.getId());
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
            orderResponse.setOrderStatus(order.getOrderStatus());
            orderResponse.setDeliveryAddress(order.getDeliveryAddress());

            orderResponses.add(orderResponse);
        }

        return new ServiceResponse<>(orderResponses, "All orders retrieved successfully.", HttpStatus.OK);


    }







    @Transactional
    public ServiceResponse<String> cancelOrder(int orderId, HttpServletRequest req) {
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Check if the order belongs to the customer
        Orders order = orderRepository.findByIdAndCustomerId(Long.valueOf(orderId), customer.getId());
        if (order == null) {
            return new ServiceResponse<>(null, "Order not found or does not belong to the customer.", HttpStatus.NOT_FOUND);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
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

    public Orders findOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public ServiceResponse<Payment> getPaymentDetails(String paymentId) {
        System.out.println("Payment id recieved inside the getPaymentREsponse" + paymentId);
        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
            Payment payment = razorpay.payments.fetch(paymentId);
            String status = payment.get("status");
            System.out.println("Status is : " + status);
            return new ServiceResponse<>(payment, "Payment details retrieved successfully.", HttpStatus.OK);
        } catch (RazorpayException e) {
            return new ServiceResponse<>(null, "Error in processing payment.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
