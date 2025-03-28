package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyProfitDTO {
    private LocalDate date;
    private BigDecimal profit;
}
