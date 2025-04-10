package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando ocurre un error
 * durante el proceso de registro de un nuevo usuario.
 */
public class UserRegisterException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code UserRegisterException} con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error ocurrido en el registro.
     */
    public UserRegisterException(String message) {
        super(message);
    }
}
