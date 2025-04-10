package com.imperial_net.inventioryApp.exceptions;

/**
 * Excepción personalizada que se lanza cuando ocurre un error
 * relacionado con la gestión de proveedores en el sistema.
 */
public class ProviderException extends RuntimeException {

  /**
   * Crea una nueva instancia de {@code ProviderException} con el mensaje especificado.
   *
   * @param message mensaje descriptivo del error.
   */
  public ProviderException(String message) {
    super(message);
  }
}
