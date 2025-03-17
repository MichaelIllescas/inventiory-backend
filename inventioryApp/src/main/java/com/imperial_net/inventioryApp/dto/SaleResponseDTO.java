package com.imperial_net.inventioryApp.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleResponseDTO {

    private Long id;
    private LocalDateTime saleDate;
    private List<SaleDetailDTO> saleDetails;
    private BigDecimal totalSale;
    private BigDecimal totalCost;
    private BigDecimal grossProfit;
    private BigDecimal discountApplied;
    private BigDecimal netProfit;
    private String paymentMethod;
    private String status;
    private Long clientId; // Si la venta tiene un cliente, se devuelve el ID
}
