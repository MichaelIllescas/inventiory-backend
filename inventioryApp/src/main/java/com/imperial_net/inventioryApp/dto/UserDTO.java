package com.imperial_net.inventioryApp.dto;

import com.imperial_net.inventioryApp.models.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String lastname;
    private String documentNumber;
    private String phone;
    private String addres;
    private String email;
    private String state;
    private Role role;
}