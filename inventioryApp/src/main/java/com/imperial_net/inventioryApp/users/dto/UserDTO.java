package com.imperial_net.inventioryApp.users.dto;

import com.imperial_net.inventioryApp.users.model.Role;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import lombok.*;

/**
 * DTO utilizado para representar los datos de un usuario en el sistema.
 * Contiene los detalles de un usuario, incluyendo información personal,
 * estado, rol y suscripción.
 */
@Data               // Genera automáticamente los métodos getter, setter, toString, equals y hashCode
@NoArgsConstructor  // Genera un constructor sin parámetros
@AllArgsConstructor // Genera un constructor con todos los parámetros
@Builder            // Proporciona un patrón de diseño de constructor con el uso del builder
public class UserDTO {

    /**
     * Identificador único del usuario en la base de datos.
     */
    private Long id;

    /**
     * Primer nombre del usuario.
     */
    private String firstName;

    /**
     * Apellido del usuario.
     */
    private String lastName;

    /**
     * Número de documento del usuario.
     */
    private String documentNumber;

    /**
     * Número de teléfono del usuario.
     */
    private String phone;

    /**
     * Dirección del usuario.
     */
    private String address;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String email;

    /**
     * Estado del usuario (activo, inactivo, etc.).
     */
    private String state;

    /**
     * Rol asignado al usuario (por ejemplo, ADMIN, USER).
     */
    private Role role;

    /**
     * Fecha de registro del usuario en el sistema.
     */
    private String registrationDate;

    /**
     * Suscripción del usuario (FREE o PRO).
     */
    private Subscription subscription;
}
