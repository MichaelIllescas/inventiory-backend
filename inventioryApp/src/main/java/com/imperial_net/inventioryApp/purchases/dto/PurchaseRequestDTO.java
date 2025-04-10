package com.imperial_net.inventioryApp.purchases.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para registrar o actualizar una compra.
 * Contiene los datos necesarios para almacenar una compra en el sistema.
 */
@Getter
@Setter
@Data
public class PurchaseRequestDTO {

    /**
     * Código del producto (requerido si no se pasa el ID).
     */
    @NotNull(message = "El código del producto es obligatorio.")
    private String productCode;

    /**
     * ID del producto (opcional si se envía el código).
     */
    private Long productId;

    /**
     * ID del proveedor asociado a la compra.
     */
    @NotNull(message = "El ID del proveedor es obligatorio.")
    private Long providerId;

    /**
     * Precio de compra del producto (debe ser mayor a 0).
     */
    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    private BigDecimal purchasePrice;

    /**
     * Cantidad comprada (debe ser al menos 1).
     */
    @NotNull(message = "La cantidad comprada es obligatoria.")
    @Min(value = 1, message = "La cantidad comprada debe ser al menos 1.")
    private BigDecimal quantity;

    /**
     * Fecha en la que se realizó la compra.
     */
    @NotNull(message = "La fecha de compra es obligatoria.")
    @PastOrPresent(message = "La fecha de compra no puede ser en el futuro.")
    private LocalDate purchaseDate;

    /**
     * Observaciones o notas adicionales de la compra.
     */
    private String notes;

    /**
     * Estado de la compra (por defecto: ACTIVO).
     */
    private String state = "ACTIVO";
}
