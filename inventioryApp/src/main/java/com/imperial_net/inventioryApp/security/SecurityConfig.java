package com.imperial_net.inventioryApp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Configuración de seguridad para la aplicación.
 * Aquí se definen las reglas de acceso a las rutas y cómo manejar la autenticación y autorización.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;  // Filtro para manejar la autenticación mediante cookies.
    private final AuthenticationProvider authenticationProvider;  // Proveedor de autenticación.

    /**
     * Configura el filtrado de seguridad para la aplicación web.
     *
     * @param http objeto HttpSecurity que permite configurar los filtros y reglas de seguridad.
     * @return SecurityFilterChain con la configuración de seguridad.
     * @throws Exception en caso de error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configura CORS (Cross-Origin Resource Sharing), permitiendo solicitudes desde ciertos orígenes
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    // Se permiten solicitudes desde localhost y ciertas rutas específicas
                    corsConfiguration.setAllowedOriginPatterns(Arrays.asList(
                            "http://localhost*",
                            "http://localhost",
                            "/auth/forgot-password",
                            "/auth/reset-password"
                    ));
                    // Métodos HTTP permitidos en CORS
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
                    // Se permite cualquier encabezado
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.setAllowCredentials(true);  // Se permiten credenciales como cookies
                    return corsConfiguration;
                }))
                .csrf().disable()  // Desactiva la protección CSRF ya que se usa JWT
                .authorizeHttpRequests()
                // Se permite el acceso sin autenticación a las rutas de autenticación y suscripciones
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/subsciption/**").permitAll()
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
                .and()
                // Configura la política de sesiones para que no haya sesiones persistentes
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Establece el proveedor de autenticación
                .authenticationProvider(authenticationProvider)
                // Añade el filtro para manejar la autenticación mediante JWT en las cookies
                .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();  // Devuelve la configuración construida
    }
}
