package com.imperial_net.inventioryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "El tipo de gasto es obligatorio.")
    @Size(max = 100, message = "El tipo de gasto no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String expenseType;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    @Column(nullable = false)
    private Double amount;

    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    @Column(nullable = false, length = 50)
    private String paymentMethod;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDate.now();
    }
}
