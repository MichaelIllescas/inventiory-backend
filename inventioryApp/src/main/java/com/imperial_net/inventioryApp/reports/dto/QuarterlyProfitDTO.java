package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa la rentabilidad de un trimestre específico.
 */
@Data
@AllArgsConstructor
public class QuarterlyProfitDTO {

    /**
     * Nombre del trimestre (ejemplo: "Q1", "Q2").
     */
    private String quarter;

    /**
     * Ingresos totales del trimestre.
     */
    private BigDecimal income;

    /**
     * Costo total de los productos vendidos en el trimestre.
     */
    private BigDecimal productCost;

    /**
     * Total de gastos registrados en el trimestre.
     */
    private BigDecimal expenses;

    /**
     * Ganancia neta del trimestre (ingresos - costos - gastos).
     */
    private BigDecimal netProfit;

    /**
     * Rentabilidad del trimestre en porcentaje.
     */
    private BigDecimal percentage;
}
