/**
 * Servicio encargado de gestionar cookies de autenticación,
 * incluyendo la creación de cookies con JWT y la extracción de usuarios desde ellas.
 */
package com.imperial_net.inventioryApp.auth.service;

import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
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

    /**
     * Crea una cookie HTTP que contiene el token JWT para mantener la sesión activa.
     *
     * @param jwt el token JWT generado para el usuario autenticado
     * @return cookie de respuesta configurada
     */
    public ResponseCookie createAuthCookie(String jwt) {
        return ResponseCookie.from("authToken", jwt)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(3600)
                .build();
    }

    /**
     * Obtiene el usuario autenticado a partir del token JWT presente en la cookie.
     *
     * @param request la petición HTTP entrante
     * @return el usuario autenticado si la cookie y el token son válidos
     */
    public Optional<User> getUserFromCookie(HttpServletRequest request) {
        String token = getAuthTokenFromCookies(request);
        if (token == null) {
            return Optional.empty();
        }

        String username = jwtService.extractUserName(token);
        if (username == null) {
            return Optional.empty();
        }

        return userRepository.findByEmail(username);
    }

    /**
     * Extrae el token JWT desde las cookies de la petición HTTP.
     *
     * @param request petición HTTP entrante
     * @return valor del token JWT o null si no se encuentra
     */
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
