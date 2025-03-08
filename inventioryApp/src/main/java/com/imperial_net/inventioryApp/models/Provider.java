package com.imperial_net.inventioryApp.models;


import com.imperial_net.inventioryApp.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "providers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio.")
    @Size(max = 100, message = "El nombre del proveedor no puede superar los 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 150, message = "El nombre comercial no puede superar los 150 caracteres.")
    private String businessName;

    @Pattern(regexp = "\\d{11}", message = "El CUIT/CUIL debe contener 11 dígitos numéricos.")
    private String taxId;

    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe contener entre 7 y 15 dígitos numéricos, opcionalmente con un '+' al inicio.")
    private String phone;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;

    @Size(max = 100, message = "El sitio web no puede superar los 100 caracteres.")
    private String website;

    @Size(max = 100, message = "El nombre del contacto no puede superar los 100 caracteres.")
    private String contactPerson;

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres.")
    private String notes;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy; // Usuario que registró el proveedor

    private LocalDate registrationDate;
    private LocalDate updateDate;

    private Boolean state;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.state=true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}
