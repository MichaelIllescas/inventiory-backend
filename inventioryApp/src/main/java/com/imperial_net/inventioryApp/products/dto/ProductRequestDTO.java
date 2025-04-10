package com.imperial_net.inventioryApp.products.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para registrar o actualizar productos.
 * Contiene los datos requeridos para validar la creación o edición de un producto.
 */
@Getter
@Setter
public class ProductRequestDTO {

    /**
     * Código único del producto.
     */
    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres.")
    private String code;

    /**
     * Nombre del producto.
     */
    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres.")
    private String name;

    /**
     * Descripción opcional del producto.
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    /**
     * Precio de venta del producto.
     */
    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    private BigDecimal salePrice;

    /**
     * Stock mínimo definido para alertas de bajo inventario.
     */
    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private BigDecimal minStock;

    /**
     * Categoría del producto (ej. Electrónica, Indumentaria, etc.).
     */
    @NotBlank(message = "La categoría es obligatoria.")
    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
    private String category;

    /**
     * Nombre de la marca asociada al producto.
     * Si no existe, será creada automáticamente.
     */
    @Size(max = 100, message = "El nombre de la marca no puede superar los 100 caracteres.")
    private String brandName;
}
