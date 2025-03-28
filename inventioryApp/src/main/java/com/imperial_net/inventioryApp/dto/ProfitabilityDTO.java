package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ProfitabilityDTO {
    private int year;
    private BigDecimal income;
    private BigDecimal totalCost;
    private BigDecimal expenses;
    private BigDecimal netProfit;
    private BigDecimal profitabilityPercentage;
    private List<QuarterlyProfitDTO> quarterlyData;

}
