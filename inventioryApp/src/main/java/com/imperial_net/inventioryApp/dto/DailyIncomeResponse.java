package com.imperial_net.inventioryApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DailyIncomeResponse {
    private BigDecimal grossIncome;  // Ingresos Brutos (Total de ventas)
    private BigDecimal totalCost;    // Costo total de los productos vendidos
    private BigDecimal grossProfit;  // Ganancia Bruta (Ingresos - Costos)
}
