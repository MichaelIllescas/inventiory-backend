package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando ocurre un error
 * relacionado con la gestión de gastos en el sistema.
 */
public class ExpenseException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code ExpenseException} con el mensaje especificado.
     *
     * @param message el mensaje descriptivo del error.
     */
    public ExpenseException(String message) {
        super(message);
    }
}
