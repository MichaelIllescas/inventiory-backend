/**
 * DTO que representa la información de un cliente al ser devuelto desde el backend.
 * Incluye detalles personales, contacto, y metadatos de registro y modificación.
 */
package com.imperial_net.inventioryApp.clients.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDTO {

    /**
     * Identificador único del cliente.
     */
    private Long id;

    /**
     * Nombre del cliente.
     */
    private String name;

    /**
     * Apellido del cliente.
     */
    private String lastname;

    /**
     * Número de documento del cliente.
     */
    private String documentNumber;

    /**
     * Identificador externo opcional del cliente.
     */
    private String laxId;

    /**
     * Correo electrónico del cliente.
     */
    private String email;

    /**
     * Número de teléfono del cliente.
     */
    private String phone;

    /**
     * Dirección del cliente.
     */
    private String address;

    /**
     * Nombre del usuario que registró al cliente.
     */
    private String createdBy;

    /**
     * Fecha de registro del cliente (formato String).
     */
    private String registrationDate;

    /**
     * Fecha de última actualización del cliente (formato String).
     */
    private String updateDate;

    /**
     * Estado del cliente (activo/inactivo).
     */
    private boolean active;
}