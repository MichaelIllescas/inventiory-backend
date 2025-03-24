package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardResponseDTO {

    private BigDecimal totalNetProfit;
    private BigDecimal currentMonthProfit;
    private BigDecimal totalProfitability;
    private BigDecimal todayProfit;
    private BigDecimal currentQuarterProfit;
    private BigDecimal totalInvestedCapital;


    private Long totalRegisteredClients;
    private Long totalRegisteredProducts;


    private List<TopSellingProductResponse> topSellingProducts;
    private List<StockLowDTO> lowStockProducts;
    private List<TopCustomerResponse> topCustomers;
    private List<DailyProfitDTO> weeklyProfits;
    private List<QuarterlyMonthProfitDTO> quarterlyProfits;

}
