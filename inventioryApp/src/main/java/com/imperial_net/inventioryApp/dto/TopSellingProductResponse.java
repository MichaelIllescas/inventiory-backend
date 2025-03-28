package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopSellingProductResponse {
    private String productName; // Nombre del producto
    private String productCode; // Código del producto
    private Long totalQuantitySold; // Cantidad total vendida
    private BigDecimal totalRevenue; // Total generado en ventas
}
