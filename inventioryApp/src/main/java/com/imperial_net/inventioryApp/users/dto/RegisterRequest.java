package com.imperial_net.inventioryApp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para registrar un nuevo usuario.
 * Contiene todos los campos necesarios para crear un usuario en el sistema.
 */
@Data               // Genera automáticamente los métodos getter, setter, toString, equals y hashCode
@Builder            // Permite usar el patrón Builder para construir el objeto
@AllArgsConstructor // Genera un constructor con todos los parámetros
@NoArgsConstructor  // Genera un constructor sin parámetros
public class RegisterRequest {

    /**
     * Nombre del usuario.
     */
    private String firstName;

    /**
     * Apellido del usuario.
     */
    private String lastName;

    /**
     * Documento Nacional de Identidad (DNI) del usuario.
     */
    private String dni;

    /**
     * Teléfono de contacto del usuario.
     */
    private String telefono;

    /**
     * Dirección del usuario.
     */
    private String direccion;

    /**
     * Correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Rol del usuario (por ejemplo, ADMIN, USER).
     */
    private String role;
}
