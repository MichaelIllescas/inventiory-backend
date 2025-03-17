package com.imperial_net.inventioryApp.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchases")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @NotNull(message = "La cantidad comprada es obligatoria.")
    @Min(value = 1, message = "La cantidad comprada debe ser al menos 1.")
    private BigDecimal quantity;

    /**
     * Stock restante de esta compra.
     * Se usa para el método FIFO en ventas.
     */
    @NotNull
    private BigDecimal remainingStock;

    @NotNull(message = "La fecha de compra es obligatoria.")
    @PastOrPresent(message = "La fecha de compra no puede ser en el futuro.")
    private LocalDate purchaseDate;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "registred_by", nullable = false)
    private User registredBy;

    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracteres.")
    private String notes;

    @NotNull(message = "El estado de la compra no puede ser nulo")
    private Boolean state;

    @PrePersist
    protected void onCreate() {
        this.remainingStock = this.quantity; // Inicializar remainingStock con la cantidad comprada
        this.state = true;
    }
}
