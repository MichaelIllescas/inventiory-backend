package com.imperial_net.inventioryApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String lastname;
    private String documentNumber;
    private String laxId;
    private String email;
    private String phone;
    private String address;
    private String createdBy;
    private String registrationDate;
    private String updateDate;
}
