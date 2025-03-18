package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class QuarterlyProfitDTO {
    private String quarter;
    private BigDecimal income;
    private BigDecimal productCost;
    private BigDecimal expenses;
    private BigDecimal netProfit;
    private BigDecimal percentage;
}
