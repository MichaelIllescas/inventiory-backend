package com.imperial_net.inventioryApp.dto;

import com.imperial_net.inventioryApp.models.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String address;
    private String email;
    private String password;
    private Role role;
}