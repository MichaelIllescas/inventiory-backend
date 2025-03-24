package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class QuarterlyMonthProfitDTO {
    private int month;
    private BigDecimal profit;
}

