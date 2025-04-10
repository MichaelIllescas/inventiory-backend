/**
 * DTO que representa la respuesta de autenticación enviada al cliente.
 * Contiene el token JWT generado tras una autenticación exitosa.
 */
package com.imperial_net.inventioryApp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * Token JWT generado para el usuario autenticado.
     */
    private String jwt;
}