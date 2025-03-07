package com.imperial_net.inventioryApp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductRequestDTO {

    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres.")
    private String code;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    private BigDecimal purchasePrice;

    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    private BigDecimal salePrice;

    @NotNull(message = "El stock disponible es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private Integer minStock;

    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
    private String category;

    @NotNull(message = "El proveedor es obligatorio.")
    private Long providerId; // Se recibe solo el ID del proveedor
    private String brandName; // Se recibe el nombre de la marca, si no existe se creará automáticamente
}
