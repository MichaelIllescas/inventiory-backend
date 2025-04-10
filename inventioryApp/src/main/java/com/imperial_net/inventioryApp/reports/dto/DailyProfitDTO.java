package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa la ganancia obtenida en un día específico.
 */
@Data
@AllArgsConstructor
public class DailyProfitDTO {

    /**
     * Fecha de la ganancia.
     */
    private LocalDate date;

    /**
     * Ganancia neta obtenida en esa fecha.
     */
    private BigDecimal profit;
}
