package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o WHERE o.customer.id = :customerId")
    List<Orders> findAllByCustomerId(int customerId);
    @Query("SELECT o FROM Orders o WHERE o.id = :orderId AND o.customer.id = :customerId")
    Orders findByIdAndCustomerId(@Param("orderId") Long orderId, @Param("customerId") int customerId);
}
