package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(int customerId);
}
