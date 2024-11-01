package com.shopping.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "product_index")
public class ProductES {
    @Id
    private Integer id;

    private String name;
    private BigDecimal price;
    private String imageURL;


}