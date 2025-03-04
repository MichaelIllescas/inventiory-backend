package com.imperial_net.inventioryApp.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class ProviderResponseDTO {
    private Long id;
    private String name;
    private String businessName;
    private String taxId;
    private String email;
    private String phone;
    private String address;
    private String website;
    private String contactPerson;
    private String notes;
    private String createdBy;
    private String registrationDate;
    private String updateDate;
}