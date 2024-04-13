package com.shopping.Ecommerce.repository;

import com.shopping.Ecommerce.entity.Cart;
import com.shopping.Ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart = :cart")
    void deleteByCart(Cart cart);
}


