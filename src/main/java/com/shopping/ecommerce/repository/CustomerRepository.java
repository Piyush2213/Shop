package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE Customer c SET c.token = ?2 WHERE c.id = ?1")
    int updateToken(int id, String token);

    @Query("SELECT c FROM Customer c WHERE c.token = ?1")
    Customer findByToken(String token);

}
