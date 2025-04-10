package com.imperial_net.inventioryApp.expenses.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa un gasto registrado por un usuario.
 * Cada gasto incluye tipo, monto, método de pago, fecha, descripción opcional y su creador.
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    /**
     * Identificador único del gasto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en la que se realizó el gasto.
     */
    @NotNull(message = "La fecha del gasto es obligatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Tipo de gasto (ej. servicios, alquiler, compras).
     */
    @NotBlank(message = "El tipo de gasto es obligatorio.")
    @Size(max = 100, message = "El tipo de gasto no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String expenseType;

    /**
     * Monto total del gasto.
     */
    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    @Column(nullable = false)
    private Double amount;

    /**
     * Método de pago utilizado (ej. efectivo, tarjeta, transferencia).
     */
    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    @Column(nullable = false, length = 50)
    private String paymentMethod;

    /**
     * Descripción opcional del gasto.
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    /**
     * Usuario que registró el gasto.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
