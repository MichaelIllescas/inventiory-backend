package com.imperial_net.inventioryApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class ProductResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer stock;
    private Integer minStock;
    private String category;
    private String providerName;
    private String registratedByName;
    private String brandName; // Nombre de la marca
    private String registrationDate;
    private String updatedDate;
    private String lastPriceUpdate;
}
