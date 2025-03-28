package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.UserRepository;
import com.imperial_net.inventioryApp.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CookieService {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public ResponseCookie createAuthCookie(String jwt) {
        return ResponseCookie.from("authToken", jwt)
                .httpOnly(true)  // Permitir acceso desde el frontend
                .secure(false)    // Solo se envía en HTTPS
                    .sameSite("Lax")  // Permite cross-origin
                //  .domain(".imperial-net.com")  // Permite compartir entre subdominios
                .path("/")
                .maxAge(3600)     // Duración de la cookie en segundos (1 hora)
                .build();
    }

    public Optional<User> getUserFromCookie(HttpServletRequest request) {
        // Extraer la cookie de autenticación
        String token = getAuthTokenFromCookies(request);
        if (token == null) {
            return Optional.empty();
        }

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUserName(token);
        if (username == null) {
            return Optional.empty();
        }

        // Buscar al usuario en la base de datos
        return userRepository.findByEmail(username);
    }

    private String getAuthTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "authToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
