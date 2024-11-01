package com.shopping.ecommerce.repository;

import com.shopping.ecommerce.entity.ProductES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchProductSearchRepository extends ElasticsearchRepository<ProductES, Integer> {
}