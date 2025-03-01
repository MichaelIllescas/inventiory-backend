package com.imperial_net.inventioryApp.controllers;



import com.imperial_net.inventioryApp.dto.AuthenticationRequest;
import com.imperial_net.inventioryApp.dto.AuthenticationResponse;
import com.imperial_net.inventioryApp.dto.UserDTO;
import com.imperial_net.inventioryApp.exceptions.DisabledUserException;
import com.imperial_net.inventioryApp.exceptions.InvalidCredentialsException;
import com.imperial_net.inventioryApp.security.JwtService;
import com.imperial_net.inventioryApp.services.AuthenticationService;
import com.imperial_net.inventioryApp.services.CookieService;
import com.imperial_net.inventioryApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        try {
            // Llamar al servicio para manejar el login
            AuthenticationResponse authResponse = authenticationService.login(request);

            // Si la autenticación es exitosa, crear y enviar la cookie
            ResponseCookie cookie = cookieService.createAuthCookie(authResponse.getJwt());
            response.addHeader("Set-Cookie", cookie.toString());

            UserDTO userDto = userService.toDTO(authenticationService.getUserByEmail(request.getEmail()).get());

            return ResponseEntity.ok(userDto); // Solo aquí se devuelve la cookie
        } catch (InvalidCredentialsException e) {
            // No enviar cookie si hay error de credenciales
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (DisabledUserException e) {
            // No enviar cookie si el usuario está deshabilitado
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al conectarse al servidor."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira de inmediato

        response.addCookie(cookie);
        return ResponseEntity.ok("Logout exitoso");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getSession(@CookieValue(name = "authToken", required = false) String jwtToken) {
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
        }

        try {
            String username = jwtService.extractUserName(jwtToken);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión inválida");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión inválida");
        }
    }
}
