package com.imperial_net.inventioryApp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String dni;
    private String telefono;
    private String direccion;
    private String email;
    private String password;
    private String role;

}
