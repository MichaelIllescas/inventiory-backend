package com.imperial_net.inventioryApp.products.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para devolver los datos detallados de un producto.
 * Se utiliza comúnmente en respuestas al cliente luego de registrar, listar o modificar productos.
 */
@Getter
@Setter
public class ProductResponseDTO {

    /**
     * ID único del producto.
     */
    private Long id;

    /**
     * Código del producto.
     */
    private String code;

    /**
     * Nombre del producto.
     */
    private String name;

    /**
     * Descripción opcional del producto.
     */
    private String description;

    /**
     * Precio de venta del producto.
     */
    private BigDecimal salePrice;

    /**
     * Stock mínimo definido para alertas.
     */
    private BigDecimal minStock;

    /**
     * Cantidad actual en stock.
     */
    private BigDecimal stock;

    /**
     * Categoría a la que pertenece el producto.
     */
    private String category;

    /**
     * Nombre del usuario que registró el producto.
     */
    private String registratedByName;

    /**
     * Nombre de la marca asociada.
     */
    private String brandName;

    /**
     * Fecha en que se registró el producto (formato string).
     */
    private String registrationDate;

    /**
     * Fecha de la última actualización (formato string).
     */
    private String updatedDate;

    /**
     * Estado actual del producto (activo/inactivo).
     */
    private String state;
}
