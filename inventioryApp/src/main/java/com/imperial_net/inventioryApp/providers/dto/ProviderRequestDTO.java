package com.imperial_net.inventioryApp.providers.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para registrar o actualizar un proveedor.
 * Contiene todos los campos necesarios para representar la información del proveedor.
 */
@Data
public class ProviderRequestDTO {

    /**
     * Nombre completo del proveedor (razón social).
     */
    @NotBlank(message = "El nombre del proveedor es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    /**
     * Nombre comercial del proveedor (opcional).
     */
    @Size(max = 150, message = "El nombre comercial no puede superar los 150 caracteres.")
    private String businessName;

    /**
     * CUIT o CUIL del proveedor (11 dígitos).
     */
    @Pattern(regexp = "\\d{11}", message = "El CUIT/CUIL debe contener 11 dígitos numéricos.")
    private String taxId;

    /**
     * Correo electrónico de contacto.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    /**
     * Teléfono de contacto (entre 7 y 15 dígitos numéricos).
     */
    @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe contener entre 7 y 15 dígitos numéricos.")
    private String phone;

    /**
     * Dirección física del proveedor.
     */
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;

    /**
     * Sitio web del proveedor (opcional).
     */
    @Size(max = 100, message = "El sitio web no puede superar los 100 caracteres.")
    private String website;

    /**
     * Persona de contacto dentro del proveedor.
     */
    @Size(max = 100, message = "El nombre del contacto no puede superar los 100 caracteres.")
    private String contactPerson;

    /**
     * Notas u observaciones adicionales.
     */
    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres.")
    private String notes;
}
