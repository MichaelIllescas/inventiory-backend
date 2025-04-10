package com.imperial_net.inventioryApp.users.model;

import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
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
import java.util.Collection;
import java.util.List;

/**
 * Clase que representa a un usuario dentro del sistema.
 * Implementa la interfaz `UserDetails` de Spring Security para la autenticación y autorización del usuario.
 *
 * Los usuarios pueden tener diferentes roles (ADMIN, USER) y suscripciones (FREE, PRO), lo que determina su acceso
 * a diversas funcionalidades en el sistema.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    /**
     * ID único del usuario, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Column(nullable = false)
    private String firstName;

    /**
     * Apellido del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "El apellido no puede estar vacío.")
    @Column(nullable = false)
    private String lastName;

    /**
     * Documento de identidad del usuario.
     * Debe ser un número con entre 7 y 10 dígitos.
     */
    @NotBlank(message = "El dni no puede estar vacío.")
    @Pattern(regexp = "\\d{7,10}", message = "El DNI debe tener entre 7 y 10 dígitos.")
    @Column(nullable = false)
    private String documentNumber;

    /**
     * Teléfono del usuario.
     * Debe ser un número con entre 6 y 15 dígitos.
     */
    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Pattern(regexp = "\\d{6,16}", message = "El Telefono debe tener entre 6 y 15 dígitos.")
    @Column(nullable = false)
    private String phone;

    /**
     * Dirección del usuario.
     * No puede estar vacía.
     */
    @NotBlank(message = "La dirección no puede estar vacía.")
    @Column(nullable = false)
    private String address;

    /**
     * Correo electrónico del usuario.
     * Debe ser único en el sistema y válido según el formato de email.
     */
    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe ser válido.")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario.
     * No puede estar vacía.
     */
    @NotBlank(message = "El password no puede estar vacío.")
    @Column(nullable = false)
    private String password;

    /**
     * Estado del usuario (habilitado o deshabilitado).
     * Se inicializa como `true` (habilitado).
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Rol del usuario dentro del sistema (ADMIN o USER).
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Fecha en la que el usuario se registró en el sistema.
     * Se asigna automáticamente al crear el usuario.
     */
    @NotNull(message = "La fecha de registro no puede estar vacía.")
    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    /**
     * Tipo de suscripción del usuario (FREE o PRO).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Subscription subscription;

    /**
     * Método que se ejecuta antes de persistir el usuario en la base de datos.
     * Asigna la fecha actual a la propiedad `registrationDate` cuando se crea el usuario.
     */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
    }

    /**
     * Implementación del método de la interfaz `UserDetails` que devuelve los roles del usuario.
     *
     * @return Una lista de roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); // Devuelve el rol del usuario como una autoridad.
    }

    /**
     * Devuelve la contraseña del usuario.
     *
     * @return La contraseña del usuario.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Devuelve el nombre de usuario (en este caso, el correo electrónico).
     *
     * @return El correo electrónico del usuario.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Verifica si la cuenta no está bloqueada.
     *
     * @return true, ya que no se permite bloquear la cuenta en este sistema.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Verifica si la cuenta no ha expirado.
     *
     * @return true, ya que no se permite que la cuenta expire en este sistema.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Verifica si las credenciales del usuario no han expirado.
     *
     * @return true, ya que las credenciales no expiran en este sistema.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Verifica si el usuario está habilitado.
     *
     * @return true si el usuario está habilitado, false si está deshabilitado.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
