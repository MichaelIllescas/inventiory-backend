package com.imperial_net.inventioryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Entity
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Sale sale;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Product product;

    /**
     * Cantidad del producto vendido (soporta valores decimales).
     */
    @NotNull
    @Positive
    private BigDecimal quantity;

    /**
     * Precio por unidad al momento de la venta.
     */
    @NotNull
    @Positive
    private BigDecimal salePrice;

    /**
     * Monto total pagado por este producto en la venta.
     * Se calcula como: salePrice * quantity.
     */
    @NotNull
    private BigDecimal subtotal;

    /**
     * Costo por unidad al momento de la venta.
     */
    @NotNull
    @Positive
    private BigDecimal costPrice;
}
