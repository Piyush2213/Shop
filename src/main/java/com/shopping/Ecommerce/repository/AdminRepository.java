package com.shopping.Ecommerce.repository;

import com.shopping.Ecommerce.entity.Admin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query("SELECT a FROM Admin a WHERE a.email = ?1")
    Admin findByEmail(String email);


    public Admin getAdminByEmailAndName(String email, String name);

    @Transactional
    @Modifying
    @Query("UPDATE Admin c SET c.token = ?2 WHERE c.id = ?1")
    int updateToken(int id, String token);

    @Query("SELECT c FROM Admin c WHERE c.token = ?1")
    Admin findByToken(String token);
}
