package com.imperial_net.inventioryApp.users.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para la solicitud de restablecimiento de contraseña.
 * Contiene los datos necesarios para cambiar la contraseña de un usuario.
 */
@Data               // Genera automáticamente los métodos getter, setter, toString, equals y hashCode
@Getter             // Genera los métodos getter
@Setter             // Genera los métodos setter
public class ResetPasswordRequest {

    /**
     * Token de restablecimiento de contraseña generado previamente.
     * Este token es utilizado para verificar la autenticidad de la solicitud.
     */
    private String token;

    /**
     * Nueva contraseña que el usuario desea establecer.
     */
    private String newPassword;
}
