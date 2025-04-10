package com.imperial_net.inventioryApp.products.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizado para actualizar el stock de un producto específico.
 */
@Data
public class ProductUpdateStockDTO {

        /**
         * Nuevo valor de stock para el producto.
         */
        private BigDecimal stock;
}
