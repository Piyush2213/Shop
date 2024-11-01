package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.OrderItem;
import com.shopping.ecommerce.entity.Orders;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.orders = :order")
    void deleteByOrder(Orders order);
}
