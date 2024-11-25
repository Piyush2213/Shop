package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
    List<Payments> findByCustomerId(int customerId);
    Payments findByRazorPayOrderId(String razorpayOrderId);

}
