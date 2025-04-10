package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando ocurre un error relacionado con la gestión de clientes.
 */
public class ClientException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code ClientException} con el mensaje especificado.
     *
     * @param message el mensaje detallando la causa del error.
     */
    public ClientException(String message) {
        super(message);
    }
}
