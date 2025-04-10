package com.imperial_net.inventioryApp.providers.model;

import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa un proveedor en el sistema.
 */
@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    /**
     * ID único del proveedor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del proveedor (razón social).
     */
    @NotBlank(message = "El nombre del proveedor es obligatorio.")
    @Size(max = 100, message = "El nombre del proveedor no puede superar los 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Nombre comercial del proveedor.
     */
    @Size(max = 150, message = "El nombre comercial no puede superar los 150 caracteres.")
    private String businessName;

    /**
     * CUIT o CUIL (11 dígitos).
     */
    @Pattern(regexp = "\\d{11}", message = "El CUIT/CUIL debe contener 11 dígitos numéricos.")
    private String taxId;

    /**
     * Correo electrónico de contacto.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    /**
     * Teléfono del proveedor.
     */
    @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe contener entre 7 y 15 dígitos numéricos, opcionalmente con un '+' al inicio.")
    private String phone;

    /**
     * Dirección del proveedor.
     */
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;

    /**
     * Sitio web del proveedor.
     */
    @Size(max = 100, message = "El sitio web no puede superar los 100 caracteres.")
    private String website;

    /**
     * Persona de contacto dentro del proveedor.
     */
    @Size(max = 100, message = "El nombre del contacto no puede superar los 100 caracteres.")
    private String contactPerson;

    /**
     * Observaciones adicionales sobre el proveedor.
     */
    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres.")
    private String notes;

    /**
     * Usuario que registró este proveedor.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    /**
     * Fecha de registro del proveedor.
     */
    private LocalDate registrationDate;

    /**
     * Fecha de última modificación.
     */
    private LocalDate updateDate;

    /**
     * Estado del proveedor (activo/inactivo).
     */
    private Boolean state;

    /**
     * Establece la fecha de creación y activa el estado antes de guardar.
     */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.state = true;
    }

    /**
     * Actualiza la fecha de modificación antes de actualizar.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}
