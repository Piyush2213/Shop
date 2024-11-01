package com.shopping.ecommerce.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import com.shopping.ecommerce.entity.ProductES;
import com.shopping.ecommerce.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.function.Supplier;


@Service
public class ElasticSearchService {
    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    public SearchResponse<ProductES> fuzzySearch(String approximateProductName) throws IOException {
        Supplier<Query> supplier = ElasticSearchUtil.createSupplierQuery(approximateProductName);
        return elasticsearchClient
                .search(s->s.index("product_index").query(supplier.get()), ProductES.class);
    }
}