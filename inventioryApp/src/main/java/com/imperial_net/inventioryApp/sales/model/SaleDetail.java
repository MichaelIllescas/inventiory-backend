package com.imperial_net.inventioryApp.sales.model;

import com.imperial_net.inventioryApp.products.models.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entidad que representa un detalle de una venta.
 * Cada instancia de esta clase corresponde a un producto específico vendido en una venta.
 */
@Data
@Entity
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación con la entidad `Sale`, donde se almacena la venta a la que pertenece este detalle.
     * Un detalle pertenece a una venta específica.
     */
    @ManyToOne
    @NotNull
    private Sale sale;

    /**
     * Relación con la entidad `Product`, indicando el producto que está siendo vendido en este detalle.
     * Un detalle contiene un producto específico.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Product product;

    /**
     * Cantidad del producto vendido (soporta valores decimales).
     * Este valor debe ser positivo, ya que no se permiten cantidades negativas.
     */
    @NotNull
    @Positive
    private BigDecimal quantity;

    /**
     * Precio de venta por unidad del producto al momento de la venta.
     * Este valor debe ser positivo, ya que no se permite un precio negativo.
     */
    @NotNull
    @Positive
    private BigDecimal salePrice;

    /**
     * Monto total pagado por este producto en la venta.
     * Se calcula como: `salePrice * quantity`.
     */
    @NotNull
    private BigDecimal subtotal;

    /**
     * Costo por unidad del producto al momento de la venta.
     * Este valor debe ser positivo, ya que no se permite un costo negativo.
     */
    @NotNull
    @Positive
    private BigDecimal costPrice;
}
