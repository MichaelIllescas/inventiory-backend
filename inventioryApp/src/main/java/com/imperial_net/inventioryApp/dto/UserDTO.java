package com.imperial_net.inventioryApp.dto;

import com.imperial_net.inventioryApp.models.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String address;
    private String email;
    private String state;
    private Role role;
    private String registrationDate;
}