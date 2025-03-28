package com.imperial_net.inventioryApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal salePrice;
    private BigDecimal minStock;
    private BigDecimal stock;
    private String category;
    private String registratedByName;
    private String brandName; // Nombre de la marca
    private String registrationDate;
    private String updatedDate;
    private String state;
}
