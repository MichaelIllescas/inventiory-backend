package com.imperial_net.inventioryApp.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "El dni no puede estar vacío.")
    @Pattern(regexp = "\\d{7,10}", message = "El DNI debe tener entre 7 y 10 dígitos.")
    @Column(nullable = false)
    private String documentNumber;

    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Pattern(regexp = "\\d{6,16}", message = "El Telefono debe tener entre 6 y 15 dígitos.")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "La dirección no puede estar vacía.")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe ser válido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El password no puede estar vacío.")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull(message = "La fecha de registro no puede estar vacía.")
    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now(); // Asigna la fecha actual al crear
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled(){
        return this.enabled;
    }
}
