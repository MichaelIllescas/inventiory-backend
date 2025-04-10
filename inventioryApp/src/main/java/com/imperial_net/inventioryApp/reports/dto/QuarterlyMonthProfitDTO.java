package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa la ganancia neta de un mes específico dentro de un trimestre.
 */
@Data
@AllArgsConstructor
public class QuarterlyMonthProfitDTO {

    /**
     * Número del mes (1 = enero, 2 = febrero, ..., 12 = diciembre).
     */
    private int month;

    /**
     * Ganancia neta correspondiente a ese mes.
     */
    private BigDecimal profit;
}
