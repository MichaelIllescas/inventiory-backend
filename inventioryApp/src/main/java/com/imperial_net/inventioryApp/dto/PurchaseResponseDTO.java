package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private String productName;
    private String productCode;
    private BigDecimal purchasePrice;
    private BigDecimal quantity;
    private String purchaseDate;
    private String providerName;
    private String notes;
    private String state;



}

