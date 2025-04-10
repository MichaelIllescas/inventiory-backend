/**
 * Entidad que representa un cliente en la base de datos.
 * Contiene información personal, contacto, metadatos de auditoría y su relación con el usuario que lo creó.
 */
package com.imperial_net.inventioryApp.clients.models;

import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    /**
     * Identificador único del cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del cliente.
     */
    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Apellido del cliente.
     */
    @NotBlank(message = "El apellido del cliente es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres.")
    private String lastname;

    /**
     * Número de documento del cliente (entre 7 y 15 dígitos).
     */
    @NotBlank(message = "El número de documento es obligatorio.")
    @Pattern(regexp = "\\d{7,15}", message = "El número de documento debe contener entre 7 y 15 dígitos.")
    private String documentNumber;

    /**
     * Identificador externo opcional del cliente.
     */
    private String laxId;

    /**
     * Correo electrónico del cliente.
     */
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
    private String email;

    /**
     * Teléfono del cliente (entre 6 y 15 dígitos, opcionalmente con '+').
     */
    @Pattern(regexp = "\\+?\\d{6,15}", message = "El teléfono debe contener entre 6 y 15 dígitos numéricos, opcionalmente con un '+' al inicio.")
    private String phone;

    /**
     * Dirección del cliente.
     */
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres.")
    private String address;

    /**
     * Usuario que creó el cliente (relación many-to-one).
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    /**
     * Fecha de registro del cliente.
     */
    private LocalDate registrationDate;

    /**
     * Fecha de última actualización del cliente.
     */
    private LocalDate updateDate;

    /**
     * Estado del cliente (activo/inactivo).
     */
    private Boolean active;

    /**
     * Establece la fecha de registro y el estado activo antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.active = true;
    }

    /**
     * Actualiza la fecha de modificación antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}