package com.imperial_net.inventioryApp.products.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa la cantidad asociada a un producto,
 * utilizado comúnmente en operaciones como ventas o actualizaciones de stock.
 */
@Data
public class ProductQuantityDTO {

    /**
     * ID del producto en la base de datos.
     */
    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long productId;

    /**
     * Cantidad del producto (puede incluir decimales).
     */
    @NotNull(message = "La cantidad no puede ser nula.")
    private BigDecimal quantity;
}
