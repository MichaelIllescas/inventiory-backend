package com.imperial_net.inventioryApp.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres.")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @NotNull(message = "El stock disponible es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private Integer minStock; // Para alertas de stock bajo

    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
    private String category;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider; // Relación con el proveedor

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy; // Usuario que registró el producto

    private LocalDateTime registrarionDate;
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.registrarionDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
