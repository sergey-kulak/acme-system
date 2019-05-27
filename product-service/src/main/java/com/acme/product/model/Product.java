package com.acme.product.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Product {
    private String id;
    private String brand;
    private String model;
    private BigDecimal price;
    private String description;


}
