package com.shopping.Ecommerce.repository;

import com.shopping.Ecommerce.entity.Cart;
import com.shopping.Ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
}
