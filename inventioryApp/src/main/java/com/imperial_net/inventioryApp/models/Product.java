package com.imperial_net.inventioryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @JoinColumn(name = "brand_id", nullable = true) // Relación con Marca
    private Brand brand;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User registratedBy; // Usuario que registró el producto

    private LocalDate registrationDate;
    private LocalDate updatedDate;

    @PastOrPresent(message = "La fecha de última actualización de precios no puede estar en el futuro.")
    private LocalDate lastPriceUpdate; // Nueva fecha para registrar cambios en precios

    // 🔹 Guardamos los precios anteriores en la base de datos para poder compararlos
    @Column(precision = 10, scale = 2)
    private BigDecimal previousPurchasePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal previousSalePrice;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.previousPurchasePrice = this.purchasePrice;
        this.previousSalePrice = this.salePrice;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDate.now();

        // Verificar si hubo cambios en los precios antes de actualizar la fecha
        boolean purchasePriceChanged = !this.purchasePrice.equals(this.previousPurchasePrice);
        boolean salePriceChanged = !this.salePrice.equals(this.previousSalePrice);

        if (purchasePriceChanged || salePriceChanged) {
            this.lastPriceUpdate = LocalDate.now();
        }

        // Actualizar los valores previos
        this.previousPurchasePrice = this.purchasePrice;
        this.previousSalePrice = this.salePrice;
    }
}
