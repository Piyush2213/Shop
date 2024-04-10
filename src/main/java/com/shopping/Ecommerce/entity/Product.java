package com.shopping.Ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "gender")
    private String gender;

    @Column(name = "category")
    private String category;

    @Column(name = "subCategory")
    private String subCategory;

    @Column(name = "productType")
    private String productType;

    @Column(name = "colour")
    private String colour;

    @Column(name = "name")
    private String name;

    @Column(name = "imageURL")
    private String imageURL;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private Integer quantity;

    public Product(int id, String gender, String category, String subCategory, String productType, String colour, String name, String imageURL) {
        this.id = id;
        this.gender = gender;
        this.category = category;
        this.subCategory = subCategory;
        this.productType = productType;
        this.colour = colour;
        this.name = name;
        this.imageURL = imageURL;
    }
}
