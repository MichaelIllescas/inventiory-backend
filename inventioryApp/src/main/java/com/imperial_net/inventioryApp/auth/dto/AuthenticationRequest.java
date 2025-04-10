/**
 * DTO que representa la solicitud de autenticación de un usuario.
 * Contiene las credenciales necesarias para iniciar sesión.
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
public class AuthenticationRequest {

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;
}