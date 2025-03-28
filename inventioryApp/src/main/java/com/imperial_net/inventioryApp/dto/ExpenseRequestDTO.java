package com.imperial_net.inventioryApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseRequestDTO {

    @NotBlank(message = "El tipo de gasto es obligatorio.")
    @Size(max = 100, message = "El tipo de gasto no puede superar los 100 caracteres.")
    private String expenseType;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    private Double amount;

    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    private String paymentMethod;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")  // Asegura la deserialización y serialización correcta de la fecha en formato ISO
    private LocalDate expenseDate;
}
