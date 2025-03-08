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

    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private BigDecimal minStock;

    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
    private String category;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User registratedBy;

    private LocalDate registrationDate;
    private LocalDate updatedDate;

    @PastOrPresent(message = "La fecha de última actualización de precios no puede estar en el futuro.")
    private LocalDate lastPriceUpdate;

    @Column(precision = 10, scale = 2)
    private BigDecimal previousSalePrice;

    // ⚡ **Stock ahora es decimal**
    @NotNull(message = "El stock es obligatorio.")
    @DecimalMin(value = "0.0", message = "El stock no puede ser negativo.")
    @Column(nullable = false, precision = 10, scale = 3) // Permite 3 decimales
    private BigDecimal stock = BigDecimal.ZERO;

    private Boolean state;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.previousSalePrice = this.salePrice;
        this.stock = BigDecimal.ZERO; // ⚡ Inicializamos stock en 0
        this.state=true;
    }

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
