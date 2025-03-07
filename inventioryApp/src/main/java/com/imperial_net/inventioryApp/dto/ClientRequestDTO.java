package com.imperial_net.inventioryApp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequestDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    @NotBlank(message = "El apellido del cliente es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres.")
    private String lastname;

    @NotBlank(message = "El número de documento es obligatorio.")
    @Pattern(regexp = "\\d{7,15}", message = "El número de documento debe contener entre 7 y 15 dígitos numéricos.")
    private String documentNumber;


    private String laxId;

    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe contener entre 7 y 15 dígitos numéricos, opcionalmente con un '+' al inicio.")
    private String phone;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;
}
