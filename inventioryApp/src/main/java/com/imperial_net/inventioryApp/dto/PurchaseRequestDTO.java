package com.imperial_net.inventioryApp.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Data
public class PurchaseRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio.")
    private Long productId;

    @NotNull(message = "El ID del proveedor es obligatorio.")
    private Long providerId;

    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    private BigDecimal purchasePrice;

    @NotNull(message = "La cantidad comprada es obligatoria.")
    @Min(value = 1, message = "La cantidad comprada debe ser al menos 1.")
    private BigDecimal quantity;

    @NotNull(message = "La fecha de compra es obligatoria.")
    @PastOrPresent(message = "La fecha de compra no puede ser en el futuro.")
    private LocalDate purchaseDate;

    private String notes;

    private String state;



}
