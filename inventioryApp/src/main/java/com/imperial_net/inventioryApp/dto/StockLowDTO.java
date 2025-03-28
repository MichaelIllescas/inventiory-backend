package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StockLowDTO {

    private String code;
    private String name;
    private BigDecimal stock;
        private BigDecimal minStock;

}