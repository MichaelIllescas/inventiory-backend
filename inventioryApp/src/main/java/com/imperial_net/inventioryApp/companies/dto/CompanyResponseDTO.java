/**
 * DTO que representa la información de una empresa al ser devuelta por el backend.
 * Incluye datos generales, contacto, usuario asociado y fechas de registro/modificación.
 */
package com.imperial_net.inventioryApp.companies.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDTO {

    /**
     * Identificador único de la empresa.
     */
    private Long id;

    /**
     * Nombre de la empresa.
     */
    private String name;

    /**
     * Dirección comercial de la empresa.
     */
    private String businessAddress;

    /**
     * CUIT o número de identificación fiscal.
     */
    private String taxIdentificationNumber;

    /**
     * Teléfono de contacto de la empresa.
     */
    private String phone;

    /**
     * Correo electrónico de la empresa.
     */
    private String email;

    /**
     * ID del usuario que registró la empresa.
     */
    private Long userId;

    /**
     * Fecha de registro de la empresa (formato dd/MM/yyyy).
     */
    private String registrationDate;

    /**
     * Fecha de última actualización (formato dd/MM/yyyy).
     */
    private String updateDate;
} 