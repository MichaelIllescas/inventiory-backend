package com.imperial_net.inventioryApp.providers.dto;

import lombok.Data;

/**
 * DTO de respuesta para devolver los datos de un proveedor al frontend.
 */
@Data
public class ProviderResponseDTO {

    /**
     * Identificador único del proveedor.
     */
    private Long id;

    /**
     * Nombre completo del proveedor (razón social).
     */
    private String name;

    /**
     * Nombre comercial del proveedor.
     */
    private String businessName;

    /**
     * CUIT o CUIL del proveedor.
     */
    private String taxId;

    /**
     * Correo electrónico de contacto.
     */
    private String email;

    /**
     * Número de teléfono de contacto.
     */
    private String phone;

    /**
     * Dirección física del proveedor.
     */
    private String address;

    /**
     * Sitio web del proveedor.
     */
    private String website;

    /**
     * Persona de contacto dentro de la empresa.
     */
    private String contactPerson;

    /**
     * Notas u observaciones adicionales.
     */
    private String notes;

    /**
     * Usuario que registró al proveedor.
     */
    private String createdBy;

    /**
     * Fecha de registro del proveedor.
     */
    private String registrationDate;

    /**
     * Fecha de última actualización del proveedor.
     */
    private String updateDate;

    /**
     * Estado del proveedor (ej: ACTIVO / INACTIVO).
     */
    private String state;
}
