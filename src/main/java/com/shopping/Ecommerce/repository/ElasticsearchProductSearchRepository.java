package com.shopping.Ecommerce.repository;

import com.shopping.Ecommerce.entity.ProductES;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

@EnableElasticsearchRepositories
@Repository
public interface ElasticsearchProductSearchRepository extends ElasticsearchRepository<ProductES, Integer> {
}