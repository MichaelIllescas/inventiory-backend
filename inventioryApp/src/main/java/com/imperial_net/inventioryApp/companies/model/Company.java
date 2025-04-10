/**
 * Entidad que representa una empresa en la base de datos.
 * Contiene datos fiscales, de contacto y su asociación con el usuario creador.
 */
package com.imperial_net.inventioryApp.companies.model;

import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    /**
     * Identificador único de la empresa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la empresa.
     */
    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Dirección comercial de la empresa.
     */
    @NotBlank(message = "La dirección comercial es obligatoria.")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String businessAddress;

    /**
     * CUIT o número de identificación fiscal de la empresa.
     */
    @NotBlank(message = "El CUIT es obligatorio.")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "El CUIT debe contener al menos 11 dígitos.")
    @Column(nullable = false, unique = true, length = 11)
    private String taxIdentificationNumber;

    /**
     * Teléfono de contacto de la empresa.
     */
    @Pattern(regexp = "\\+?\\d{6,15}", message = "El teléfono debe contener entre 6 y 15 dígitos, opcionalmente comenzando con '+'.")
    @Column(length = 15)
    private String phone;

    /**
     * Correo electrónico de la empresa.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    @Column(length = 100)
    private String email;

    /**
     * Usuario asociado a la empresa (relación uno a uno).
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Fecha de registro de la empresa.
     */
    private LocalDate registrationDate;

    /**
     * Fecha de última actualización de la empresa.
     */
    private LocalDate updateDate;

    /**
     * Asigna la fecha de registro automáticamente al persistir.
     */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
    }

    /**
     * Asigna la fecha de modificación automáticamente al actualizar.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}