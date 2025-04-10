package com.imperial_net.inventioryApp.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa a un cliente destacado en base a sus compras.
 */
@Data
@AllArgsConstructor
public class TopCustomerResponse {

    /**
     * Nombre completo del cliente.
     */
    private String customerName;

    /**
     * Número de documento del cliente (DNI, CUIT, etc.).
     */
    private String documentNumber;

    /**
     * Monto total gastado por el cliente en el período analizado.
     */
    private BigDecimal totalSpent;

    /**
     * Cantidad total de compras realizadas por el cliente.
     */
    private Long totalPurchases;
}
