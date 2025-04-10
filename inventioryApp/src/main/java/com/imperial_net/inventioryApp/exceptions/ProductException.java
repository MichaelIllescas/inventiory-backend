package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando ocurre un error
 * relacionado con la gestión de productos en el sistema.
 */
public class ProductException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code ProductException} con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error.
     */
    public ProductException(String message) {
        super(message);
    }
}
