package com.imperial_net.inventioryApp.sales.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleDetailDTO {

    /**
     * ID del detalle de la venta.
     * Este identificador es único para cada registro del detalle de la venta.
     */
    private Long id;

    /**
     * ID de la venta a la que pertenece este detalle.
     * Permite asociar el detalle con la venta principal.
     */
    private Long saleId;

    /**
     * ID del producto relacionado con este detalle.
     * Referencia al producto vendido.
     */
    private Long productId;

    /**
     * Código del producto.
     * Es un identificador único del producto en el sistema.
     */
    private String productCode;

    /**
     * Nombre del producto.
     * Este es el nombre visible del producto en el sistema.
     */
    private String productName;

    /**
     * Descripción del producto.
     * Proporciona detalles adicionales sobre el producto vendido.
     */
    private String productDescription;

    /**
     * Categoría del producto.
     * Especifica a qué categoría pertenece el producto.
     */
    private String productCategory;

    /**
     * Nombre de la marca del producto.
     * Indica la marca o fabricante del producto.
     */
    private String productBrandName;

    /**
     * Cantidad del producto vendida.
     * Especifica la cantidad de unidades de este producto vendidas en esta transacción.
     */
    private BigDecimal productQuantity;

    /**
     * Precio de venta unitario del producto.
     * Es el precio por unidad del producto en la venta.
     */
    private BigDecimal productSalePrice;

    /**
     * Monto total pagado por este producto en la venta.
     * Calculado como: salePrice * quantity.
     */
    private BigDecimal subtotal;

    /**
     * Precio de costo del producto.
     * Representa el costo de adquisición del producto para la empresa.
     */
    private BigDecimal costPrice;
}
