/**
 * DTO utilizado para registrar o actualizar un cliente.
 * Incluye validaciones para los campos requeridos.
 */
package com.imperial_net.inventioryApp.clients.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequestDTO {

    /**
     * Nombre del cliente. Requerido y con un máximo de 100 caracteres.
     */
    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    /**
     * Apellido del cliente. Requerido y con un máximo de 100 caracteres.
     */
    @NotBlank(message = "El apellido del cliente es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres.")
    private String lastname;

    /**
     * Número de documento. Solo números entre 7 y 15 dígitos.
     */
    @NotBlank(message = "El número de documento es obligatorio.")
    @Pattern(regexp = "\\d{7,15}", message = "El número de documento debe contener entre 7 y 15 dígitos numéricos.")
    private String documentNumber;

    /**
     * Identificador de cliente externo (opcional).
     */
    private String laxId;

    /**
     * Correo electrónico del cliente. Debe ser válido y no superar los 100 caracteres.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    /**
     * Número de teléfono. Entre 7 y 15 dígitos, con posibilidad de comenzar con '+'.
     */
    @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe contener entre 7 y 15 dígitos numéricos, opcionalmente con un '+' al inicio.")
    private String phone;

    /**
     * Dirección del cliente. Máximo 255 caracteres.
     */
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;
}