package com.imperial_net.inventioryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la marca es obligatorio.")
    @Size(max = 100, message = "El nombre de la marca no puede superar los 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
