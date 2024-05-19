package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findBySubCategoryIn(List<String> subcategories, Pageable pageable);
}
