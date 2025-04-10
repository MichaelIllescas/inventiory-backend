package com.imperial_net.inventioryApp.users.dto;

import com.imperial_net.inventioryApp.users.model.Role;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import lombok.*;

/**
 * DTO utilizado para representar los datos necesarios para registrar un usuario
 * en el sistema. Este DTO es utilizado cuando se registra un usuario, permitiendo
 * la captura de su información básica como nombre, apellido, número de documento,
 * teléfono, dirección, correo electrónico, contraseña, rol y suscripción.
 */
@AllArgsConstructor    // Genera un constructor con todos los parámetros
@NoArgsConstructor     // Genera un constructor sin parámetros
@Getter               // Genera el getter para todos los campos de la clase
@Setter               // Genera el setter para todos los campos de la clase
@Builder              // Genera un patrón Builder para crear objetos de la clase
public class UserRequestDTO {

    /**
     * Nombre del usuario a registrar.
     */
    private String firstName;

    /**
     * Apellido del usuario a registrar.
     */
    private String lastName;

    /**
     * Número de documento del usuario (DNI, por ejemplo).
     */
    private String documentNumber;

    /**
     * Número de teléfono del usuario a registrar.
     */
    private String phone;

    /**
     * Dirección del usuario a registrar.
     */
    private String address;

    /**
     * Dirección de correo electrónico del usuario a registrar.
     */
    private String email;

    /**
     * Contraseña del usuario a registrar.
     */
    private String password;

    /**
     * Rol que se asignará al usuario (por ejemplo, ADMIN o USER).
     */
    private Role role;

    /**
     * Tipo de suscripción que el usuario tendrá (por ejemplo, FREE o PRO).
     */
    private Subscription subscription;
}
