package com.imperial_net.inventioryApp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductQuantityDTO {

    /**
     * ID del producto en la base de datos.
     */
    @NotNull
    private Long productId;

    /**
     * Cantidad vendida (puede ser decimal).
     */
    @NotNull
    private BigDecimal quantity;
}
