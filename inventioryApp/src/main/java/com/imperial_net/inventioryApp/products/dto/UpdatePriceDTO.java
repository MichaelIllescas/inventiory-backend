package com.imperial_net.inventioryApp.products.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO utilizado para modificar masivamente los precios de uno o más productos.
 * Permite definir el tipo de precio, el porcentaje de cambio y la acción a realizar.
 */
@Data
public class UpdatePriceDTO {

    /**
     * Lista de IDs de productos a los que se aplicará la modificación.
     */
    private List<Long> productIds;

    /**
     * Tipo de precio a modificar: puede ser "purchasePrice" (precio de compra)
     * o "salePrice" (precio de venta).
     */
    private String priceType;

    /**
     * Porcentaje a aplicar en la modificación.
     * Ejemplo: 10.0 para un 10%.
     */
    private Double percentage;

    /**
     * Acción a realizar sobre el precio: puede ser "increase" (incremento)
     * o "discount" (descuento).
     */
    private String action;
}
