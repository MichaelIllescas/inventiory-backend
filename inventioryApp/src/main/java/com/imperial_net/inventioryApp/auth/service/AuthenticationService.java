/**
 * Servicio encargado de gestionar la autenticación de usuarios,
 * validando credenciales y generando tokens JWT.
 */
package com.imperial_net.inventioryApp.auth.service;

import com.imperial_net.inventioryApp.auth.dto.AuthenticationResponse;
import com.imperial_net.inventioryApp.auth.dto.AuthenticationRequest;
import com.imperial_net.inventioryApp.exceptions.DisabledUserException;
import com.imperial_net.inventioryApp.exceptions.InvalidCredentialsException;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import com.imperial_net.inventioryApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Realiza el proceso de inicio de sesión validando credenciales
     * y generando un token JWT si el usuario es válido.
     *
     * @param request objeto con email y contraseña
     * @return respuesta con el token JWT generado
     * @throws InvalidCredentialsException si el usuario no existe o la contraseña es incorrecta
     * @throws DisabledUserException si el usuario está deshabilitado
     */
    public AuthenticationResponse login(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado."));

        if (!user.isEnabled()) {
            throw new DisabledUserException("El usuario está deshabilitado.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }

        Map<String, Object> claims = new HashMap<>();
        String jwt = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .jwt(jwt)
                .build();
    }

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email email del usuario
     * @return usuario encontrado (opcional)
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
