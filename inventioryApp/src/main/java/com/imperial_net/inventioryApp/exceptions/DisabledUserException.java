package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando un usuario autenticado
 * ha sido deshabilitado y no puede realizar acciones en el sistema.
 */
public class DisabledUserException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code DisabledUserException} con el mensaje proporcionado.
     *
     * @param message el mensaje que describe la causa del error.
     */
    public DisabledUserException(String message) {
        super(message);
    }
}
