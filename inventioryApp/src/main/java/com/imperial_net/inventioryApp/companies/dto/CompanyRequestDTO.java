/**
 * DTO utilizado para registrar o actualizar los datos de una empresa.
 * Incluye validaciones para todos los campos requeridos.
 */
package com.imperial_net.inventioryApp.companies.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequestDTO {

    /**
     * Nombre de la empresa.
     */
    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    /**
     * Dirección comercial de la empresa.
     */
    @NotBlank(message = "La dirección comercial es obligatoria.")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String businessAddress;

    /**
     * CUIT (identificación fiscal) de la empresa.
     */
    @NotBlank(message = "El CUIT es obligatorio.")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "El CUIT debe contener al menos 11 dígitos.")
    private String taxIdentificationNumber;

    /**
     * Teléfono de contacto de la empresa.
     */
    @Pattern(regexp = "\\+?\\d{6,15}", message = "El teléfono debe contener entre 6 y 15 dígitos, opcionalmente comenzando con '+'.")
    private String phone;

    /**
     * Correo electrónico de la empresa.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;
}