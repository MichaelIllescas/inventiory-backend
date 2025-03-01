package com.imperial_net.inventioryApp.services;



import com.imperial_net.inventioryApp.dto.AuthenticationResponse;
import com.imperial_net.inventioryApp.dto.AuthenticationRequest;
import com.imperial_net.inventioryApp.exceptions.DisabledUserException;
import com.imperial_net.inventioryApp.exceptions.InvalidCredentialsException;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.UserRepository;
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


    public AuthenticationResponse login(AuthenticationRequest request) {
        // Buscar al usuario en la base de datos
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado."));

        // Verificar si el usuario está deshabilitado
        if (!user.isEnabled()) {
            throw new DisabledUserException("El usuario está deshabilitado.");
        }

        try {
            // Verificar las credenciales del usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }

        // Generar el token JWT
        Map<String, Object> claims = new HashMap<>();
        String jwt = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .jwt(jwt)
                .build();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
