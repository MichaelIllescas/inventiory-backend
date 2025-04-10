package com.imperial_net.inventioryApp.products.models;

import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un producto en el sistema.
 * Incluye información como código, nombre, stock, precio, fechas, y relaciones con marca y usuario.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Identificador único del producto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código del producto (puede no ser único).
     */
    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres.")
    @Column(nullable = false, unique = false, length = 50)
    private String code;

    /**
     * Nombre del producto.
     */
    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descripción opcional del producto.
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    /**
     * Precio de venta actual del producto.
     */
    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    /**
     * Stock mínimo para alertas de inventario bajo.
     */
    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private BigDecimal minStock;

    /**
     * Categoría del producto (opcional).
     */
    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
    private String category;

    /**
     * Marca asociada al producto (opcional).
     */
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;

    /**
     * Usuario que registró el producto.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User registratedBy;

    /**
     * Fecha en la que se registró el producto.
     */
    private LocalDate registrationDate;

    /**
     * Fecha de última actualización del producto.
     */
    private LocalDate updatedDate;

    /**
     * Fecha en la que se actualizó el precio por última vez.
     */
    @PastOrPresent(message = "La fecha de última actualización de precios no puede estar en el futuro.")
    private LocalDate lastPriceUpdate;

    /**
     * Precio anterior antes del último cambio.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal previousSalePrice;

    /**
     * Stock actual del producto (permite decimales).
     */
    @NotNull(message = "El stock es obligatorio.")
    @DecimalMin(value = "0.0", message = "El stock no puede ser negativo.")
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal stock = BigDecimal.ZERO;

    /**
     * Estado del producto (activo/inactivo).
     */
    private Boolean state;

    /**
     * Acción ejecutada automáticamente antes de insertar un producto.
     * Establece fechas, stock inicial, precio anterior y estado.
     */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.previousSalePrice = this.salePrice;
        this.stock = BigDecimal.ZERO;
        this.state = true;
    }

    /**
     * Acción ejecutada automáticamente antes de actualizar un producto.
     * Actualiza la fecha de modificación y registra el cambio de precio si ocurrió.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDate.now();
        boolean salePriceChanged = !this.salePrice.equals(this.previousSalePrice);
        if (salePriceChanged) {
            this.lastPriceUpdate = LocalDate.now();
        }
        this.previousSalePrice = this.salePrice;
    }
}
