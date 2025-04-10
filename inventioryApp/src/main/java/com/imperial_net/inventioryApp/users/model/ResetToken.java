package com.imperial_net.inventioryApp.users.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un token de restablecimiento de contraseña para un usuario.
 * Este token se genera cuando un usuario solicita restablecer su contraseña, y
 * se utiliza para validar la solicitud antes de permitir el cambio de contraseña.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ResetToken {

    /**
     * Identificador único del token de restablecimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token de restablecimiento que se enviará al usuario.
     * El token es único y se utiliza para verificar la validez de la solicitud de restablecimiento.
     */
    private String token;

    /**
     * Fecha y hora en la que el token expirará.
     * Si el token expira, ya no podrá ser utilizado para restablecer la contraseña.
     */
    private LocalDateTime expiryDate;

    /**
     * Relación con el usuario asociado a este token.
     * Un token de restablecimiento está vinculado a un único usuario que solicitó el restablecimiento.
     */
    @OneToOne
    private User user;

    /**
     * Constructor que inicializa un nuevo token de restablecimiento con el token, usuario y fecha de expiración.
     *
     * @param token      el token generado para restablecer la contraseña.
     * @param user       el usuario que solicitó el restablecimiento.
     * @param expiryDate la fecha y hora de expiración del token.
     */
    public ResetToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }
}
