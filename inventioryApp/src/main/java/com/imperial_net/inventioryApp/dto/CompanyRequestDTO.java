package com.imperial_net.inventioryApp.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequestDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    @NotBlank(message = "La dirección comercial es obligatoria.")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String businessAddress;

    @NotBlank(message = "El CUIT es obligatorio.")
    @Pattern(regexp = "\\d{11}", message = "El CUIT debe contener exactamente 11 dígitos.")
    private String taxIdentificationNumber;

    @Pattern(regexp = "\\+?\\d{6,15}", message = "El teléfono debe contener entre 6 y 15 dígitos, opcionalmente comenzando con '+'.")
    private String phone;

    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;


}
