package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa un producto destacado por ventas.
 */
@Data
@AllArgsConstructor
public class TopSellingProductResponse {

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Código único del producto.
     */
    private String productCode;

    /**
     * Cantidad total de unidades vendidas del producto.
     */
    private Long totalQuantitySold;

    /**
     * Ingreso total generado por la venta de este producto.
     */
    private BigDecimal totalRevenue;
}
