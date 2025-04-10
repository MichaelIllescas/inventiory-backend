package com.imperial_net.inventioryApp.expenses.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO utilizado para registrar o actualizar un gasto.
 * Contiene los datos necesarios para validar y procesar la información del gasto.
 */
@Getter
@Setter
public class ExpenseRequestDTO {

    /**
     * Tipo de gasto realizado (por ejemplo: alquiler, servicios, insumos).
     */
    @NotBlank(message = "El tipo de gasto es obligatorio.")
    @Size(max = 100, message = "El tipo de gasto no puede superar los 100 caracteres.")
    private String expenseType;

    /**
     * Monto total del gasto.
     */
    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    private Double amount;

    /**
     * Método de pago utilizado (efectivo, tarjeta, transferencia, etc.).
     */
    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    private String paymentMethod;

    /**
     * Descripción adicional del gasto (opcional).
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    /**
     * Fecha en la que se realizó el gasto.
     */
    @NotNull(message = "La fecha es obligatoria.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expenseDate;
}
