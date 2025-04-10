package com.imperial_net.inventioryApp.users.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para representar los datos necesarios para el registro de un usuario
 * desde la página de aterrizaje (landing page).
 * Esta clase captura los datos que un nuevo usuario debe ingresar para registrarse,
 * incluyendo su nombre, apellido, documento, teléfono, dirección, correo electrónico
 * y la contraseña.
 */
@Data               // Genera automáticamente los métodos getter, setter, toString, equals y hashCode
@Setter            // Genera el setter para todos los campos de la clase
@Getter            // Genera el getter para todos los campos de la clase
public class UserRegisterFromLandigPageDTO {

    /**
     * Nombre del usuario que se registrará.
     */
    private String name;

    /**
     * Apellido del usuario que se registrará.
     */
    private String lastName;

    /**
     * Número de documento del usuario (DNI, por ejemplo).
     */
    private String documentNumber;

    /**
     * Número de teléfono del usuario que se registrará.
     */
    private String phone;

    /**
     * Dirección del usuario que se registrará.
     */
    private String address;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña que el usuario desea usar para su cuenta.
     */
    private String password;

    /**
     * Confirmación de la contraseña ingresada.
     * Debe coincidir con el campo `password`.
     */
    private String confirmPassword;
}
