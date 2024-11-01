package com.shopping.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "paymentLinkId", nullable = false)
    private String paymentLinkId;

    @Column(name = "referenceId", nullable = false)
    private String referenceId;

    @Column(name = "razorPayOrderId", nullable = false)
    private String razorPayOrderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
