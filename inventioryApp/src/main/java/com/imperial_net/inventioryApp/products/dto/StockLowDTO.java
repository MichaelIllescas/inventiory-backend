package com.imperial_net.inventioryApp.products.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO que representa un producto con bajo nivel de stock.
 * Utilizado para mostrar alertas o listados de productos que están por debajo del mínimo permitido.
 */
@Getter
@Setter
@AllArgsConstructor
public class StockLowDTO {

    /**
     * Código del producto.
     */
    private String code;

    /**
     * Nombre del producto.
     */
    private String name;

    /**
     * Stock actual del producto.
     */
    private BigDecimal stock;

    /**
     * Stock mínimo configurado para el producto.
     */
    private BigDecimal minStock;
}
