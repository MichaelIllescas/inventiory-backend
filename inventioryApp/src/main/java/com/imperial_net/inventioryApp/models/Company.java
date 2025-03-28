package com.imperial_net.inventioryApp.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La dirección comercial es obligatoria.")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String businessAddress;

    @NotBlank(message = "El CUIT es obligatorio.")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "El CUIT debe contener al menos 11 dígitos.")
    @Column(nullable = false, unique = true, length = 11)
    private String taxIdentificationNumber;


    @Pattern(regexp = "\\+?\\d{6,15}", message = "El teléfono debe contener entre 6 y 15 dígitos, opcionalmente comenzando con '+'.")
    @Column(length = 15)
    private String phone;

    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    @Column(length = 100)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate registrationDate;
    private LocalDate updateDate;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}