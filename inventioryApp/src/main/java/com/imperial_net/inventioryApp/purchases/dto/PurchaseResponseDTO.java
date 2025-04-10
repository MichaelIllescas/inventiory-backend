package com.imperial_net.inventioryApp.purchases.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de respuesta para representar una compra registrada en el sistema.
 */
@Data
@AllArgsConstructor
public class PurchaseResponseDTO {

    /**
     * ID único de la compra.
     */
    private Long id;

    /**
     * Nombre del producto comprado.
     */
    private String productName;

    /**
     * Código del producto comprado.
     */
    private String productCode;

    /**
     * Precio de compra unitario.
     */
    private BigDecimal purchasePrice;

    /**
     * Cantidad comprada.
     */
    private BigDecimal quantity;

    /**
     * Fecha en la que se realizó la compra (formateada como String).
     */
    private String purchaseDate;

    /**
     * Nombre del proveedor asociado a la compra.
     */
    private String providerName;

    /**
     * Notas u observaciones de la compra.
     */
    private String notes;

    /**
     * Estado de la compra (ej: ACTIVO, ANULADO).
     */
    private String state;
}
