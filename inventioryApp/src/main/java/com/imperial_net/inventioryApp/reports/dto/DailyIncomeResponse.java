package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa los ingresos diarios del usuario.
 * Contiene información sobre ingresos brutos, costos y ganancias.
 */
@Data
@AllArgsConstructor
public class DailyIncomeResponse {

    /**
     * Ingresos brutos del día (total de ventas).
     */
    private BigDecimal grossIncome;

    /**
     * Costo total de los productos vendidos durante el día.
     */
    private BigDecimal totalCost;

    /**
     * Ganancia bruta del día (grossIncome - totalCost).
     */
    private BigDecimal grossProfit;
}
