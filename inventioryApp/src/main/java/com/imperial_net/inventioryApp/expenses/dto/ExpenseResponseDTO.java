package com.imperial_net.inventioryApp.expenses.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO utilizado para devolver los datos de un gasto registrado.
 * Se utiliza en respuestas al cliente luego de registrar, consultar o actualizar un gasto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDTO {

        /**
         * Identificador único del gasto.
         */
        private Long id;

        /**
         * Fecha en la que se realizó el gasto.
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate date;

        /**
         * Tipo de gasto (ej: alquiler, servicios, compras).
         */
        private String expenseType;

        /**
         * Monto total del gasto.
         */
        private Double amount;

        /**
         * Método de pago utilizado (efectivo, tarjeta, etc.).
         */
        private String paymentMethod;

        /**
         * Descripción opcional del gasto.
         */
        private String description;

        /**
         * Nombre del usuario que registró el gasto.
         */
        private String createdBy;
}
