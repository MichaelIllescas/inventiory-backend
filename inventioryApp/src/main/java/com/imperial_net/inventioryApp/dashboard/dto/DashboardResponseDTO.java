/**
 * DTO que representa la respuesta del dashboard principal.
 * Contiene datos financieros, estadísticos y resúsmenes para el usuario autenticado.
 */
package com.imperial_net.inventioryApp.dashboard.dto;

import com.imperial_net.inventioryApp.products.dto.StockLowDTO;
import com.imperial_net.inventioryApp.reports.dto.DailyProfitDTO;
import com.imperial_net.inventioryApp.reports.dto.QuarterlyMonthProfitDTO;
import com.imperial_net.inventioryApp.reports.dto.TopCustomerResponse;
import com.imperial_net.inventioryApp.reports.dto.TopSellingProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardResponseDTO {

    /**
     * Ganancia neta total acumulada.
     */
    private BigDecimal totalNetProfit;

    /**
     * Ganancia del mes actual.
     */
    private BigDecimal currentMonthProfit;

    /**
     * Rentabilidad total.
     */
    private BigDecimal totalProfitability;

    /**
     * Ganancia del día actual.
     */
    private BigDecimal todayProfit;

    /**
     * Ganancia acumulada del trimestre actual.
     */
    private BigDecimal currentQuarterProfit;

    /**
     * Capital total invertido.
     */
    private BigDecimal totalInvestedCapital;

    /**
     * Total de clientes registrados.
     */
    private Long totalRegisteredClients;

    /**
     * Total de productos registrados.
     */
    private Long totalRegisteredProducts;

    /**
     * Lista de productos más vendidos.
     */
    private List<TopSellingProductResponse> topSellingProducts;

    /**
     * Lista de productos con stock bajo.
     */
    private List<StockLowDTO> lowStockProducts;

    /**
     * Lista de los mejores clientes por volumen de compra.
     */
    private List<TopCustomerResponse> topCustomers;

    /**
     * Lista de ganancias diarias de la semana.
     */
    private List<DailyProfitDTO> weeklyProfits;

    /**
     * Lista de ganancias mensuales del trimestre actual.
     */
    private List<QuarterlyMonthProfitDTO> quarterlyProfits;
} 