package com.imperial_net.inventioryApp.purchases.model;

import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.providers.model.Provider;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una compra de productos realizada a un proveedor.
 */
@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    /**
     * ID único de la compra.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Producto comprado.
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Precio de compra unitario del producto.
     */
    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    /**
     * Cantidad de productos comprados.
     */
    @NotNull(message = "La cantidad comprada es obligatoria.")
    @Min(value = 1, message = "La cantidad comprada debe ser al menos 1.")
    private BigDecimal quantity;

    /**
     * Stock restante de esta compra, usado para aplicar FIFO al vender.
     */
    @NotNull
    private BigDecimal remainingStock;

    /**
     * Fecha en que se realizó la compra.
     */
    @NotNull(message = "La fecha de compra es obligatoria.")
    @PastOrPresent(message = "La fecha de compra no puede ser en el futuro.")
    private LocalDate purchaseDate;

    /**
     * Proveedor al que se le realizó la compra.
     */
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    /**
     * Usuario que registró la compra.
     */
    @ManyToOne
    @JoinColumn(name = "registred_by", nullable = false)
    private User registredBy;

    /**
     * Notas u observaciones adicionales de la compra.
     */
    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracteres.")
    private String notes;

    /**
     * Estado de la compra (activo/inactivo).
     */
    @NotNull(message = "El estado de la compra no puede ser nulo")
    private Boolean state;

    /**
     * Se ejecuta antes de guardar la entidad.
     * Inicializa el stock restante con la cantidad comprada y marca la compra como activa.
     */
    @PrePersist
    protected void onCreate() {
        this.remainingStock = this.quantity;
        this.state = true;
    }
}
