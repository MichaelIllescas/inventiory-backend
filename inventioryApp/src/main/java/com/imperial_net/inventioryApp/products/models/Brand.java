package com.imperial_net.inventioryApp.products.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad que representa una marca asociada a productos.
 * Cada marca tiene un nombre único.
 */
@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    /**
     * Identificador único de la marca.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la marca.
     * Debe ser único y no puede superar los 100 caracteres.
     */
    @NotBlank(message = "El nombre de la marca es obligatorio.")
    @Size(max = 100, message = "El nombre de la marca no puede superar los 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
