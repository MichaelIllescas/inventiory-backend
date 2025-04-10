package com.imperial_net.inventioryApp.security;

import com.imperial_net.inventioryApp.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad de la aplicación.
 * Aquí se definen los beans necesarios para la autenticación y la encriptación de contraseñas.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // Repositorio de usuarios para interactuar con la base de datos.
    private final UserRepository userRepository;

    /**
     * Bean que proporciona un servicio de usuario personalizado basado en el correo electrónico.
     *
     * @return un servicio que carga el usuario desde la base de datos por correo electrónico.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                // Si no se encuentra el usuario, se lanza una excepción
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    }

    /**
     * Bean que proporciona un proveedor de autenticación que usa el servicio de detalles de usuario.
     *
     * @return el proveedor de autenticación que usa el servicio de detalles de usuario y el codificador de contraseñas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Configura el servicio de detalles de usuario para la autenticación
        authenticationProvider.setUserDetailsService(userDetailsService());
        // Configura el codificador de contraseñas
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Bean que proporciona un administrador de autenticación.
     *
     * @param configuration configuración de autenticación.
     * @return el administrador de autenticación.
     * @throws Exception si ocurre algún error en la configuración.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // Obtiene el AuthenticationManager configurado
        return configuration.getAuthenticationManager();
    }

    /**
     * Bean que configura el codificador de contraseñas usando BCrypt.
     *
     * @return el codificador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa BCrypt para encriptar las contraseñas
        return new BCryptPasswordEncoder();
    }
}
