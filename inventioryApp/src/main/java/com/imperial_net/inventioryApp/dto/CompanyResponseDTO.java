package com.imperial_net.inventioryApp.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDTO {

    private Long id;
    private String name;
    private String businessAddress;
    private String taxIdentificationNumber;
    private String phone;
    private String email;
    private Long userId;
    private String registrationDate;
    private String updateDate;
}
