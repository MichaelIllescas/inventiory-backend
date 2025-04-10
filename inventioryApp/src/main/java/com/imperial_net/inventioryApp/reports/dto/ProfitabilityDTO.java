package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que representa el resumen de rentabilidad anual del negocio.
 * Incluye información de ingresos, costos, ganancias y rentabilidad por trimestre.
 */
@Data
@AllArgsConstructor
public class ProfitabilityDTO {

    /**
     * Año al que pertenece el reporte.
     */
    private int year;

    /**
     * Ingresos totales del año.
     */
    private BigDecimal income;

    /**
     * Costo total de productos vendidos durante el año.
     */
    private BigDecimal totalCost;

    /**
     * Total de gastos registrados en el año.
     */
    private BigDecimal expenses;

    /**
     * Ganancia neta (ingresos - costos - gastos).
     */
    private BigDecimal netProfit;

    /**
     * Rentabilidad expresada en porcentaje.
     */
    private BigDecimal profitabilityPercentage;

    /**
     * Lista con los datos de rentabilidad por trimestre.
     */
    private List<QuarterlyProfitDTO> quarterlyData;
}
