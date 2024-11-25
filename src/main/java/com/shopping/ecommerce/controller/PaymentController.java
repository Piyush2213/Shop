package com.shopping.ecommerce.controller;


import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shopping.ecommerce.entity.*;
import com.razorpay.PaymentLink;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.repository.OrderRepository;
import com.shopping.ecommerce.repository.PaymentRepository;
import com.shopping.ecommerce.response.PaymentLinkResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.service.ReferenceIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, exposedHeaders = { "*" }, methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})

public class PaymentController {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String INVALID_TOKEN = "Invalid token or user not found.";

    private Customer getUserFromToken(String token) {
        return customerRepository.findByToken(token);
    }

    @PostMapping("/payments/{razorPayOrderId}")
    public ResponseEntity<ServiceResponse<PaymentLinkResponse>> createPaymentLink(@PathVariable("razorPayOrderId") String razorPayOrderId, HttpServletRequest req) {

        try {
            String token = req.getHeader(AUTHORIZATION_HEADER);
            Customer customer = getUserFromToken(token);

            if (customer == null) {
                return new ResponseEntity<>(new ServiceResponse<>(null, INVALID_TOKEN, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
            }

            Orders order = orderRepository.findByRazorPayOrderIdAndCustomerId(razorPayOrderId, customer.getId());
            if (order == null) {
                return new ResponseEntity<>(new ServiceResponse<>(null, "Order not found or does not belong to the customer.", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
            }

            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", order.getTotalAmount().multiply(new BigDecimal(100)).intValue());
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("reference_id", ReferenceIdGenerator.generateReferenceId());

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

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);


            String paymentLinkId = payment.get("id");

            String paymentLinkUrl = payment.get("short_url");

            String referenceId = payment.get("reference_id");

            order.setRazorPayOrderId(razorPayOrderId);
            order.setPaymentLinkId(paymentLinkId);
            orderRepository.save(order);

            Payments newPayment = new Payments();
            newPayment.setReferenceId(referenceId);
            newPayment.setPaymentLinkId(paymentLinkId);
            newPayment.setRazorPayOrderId(razorPayOrderId);
            newPayment.setCustomer(customer);
            paymentRepository.save(newPayment);


            PaymentLinkResponse res = new PaymentLinkResponse();
            res.setPayment_link_url(paymentLinkUrl);
            res.setPayment_link_id(paymentLinkId);
            res.setReference_id(referenceId);


            return new ResponseEntity<>(new ServiceResponse<>(res, "Payment link created successfully", HttpStatus.CREATED), HttpStatus.CREATED);

        } catch (RazorpayException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ServiceResponse<>(null, "Error in creating payment link: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
