/**
 * Controlador REST encargado de manejar la autenticación de usuarios,
 * incluyendo inicio y cierre de sesión, validación de sesión activa y
 * funcionalidades para recuperación de contraseña.
 */
package com.imperial_net.inventioryApp.auth.controller;

import com.imperial_net.inventioryApp.auth.dto.AuthenticationRequest;
import com.imperial_net.inventioryApp.auth.dto.AuthenticationResponse;
import com.imperial_net.inventioryApp.email.dto.EmailRequest;
import com.imperial_net.inventioryApp.exceptions.DisabledUserException;
import com.imperial_net.inventioryApp.exceptions.InvalidCredentialsException;
import com.imperial_net.inventioryApp.security.JwtService;
import com.imperial_net.inventioryApp.auth.service.AuthenticationService;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.users.dto.ResetPasswordRequest;
import com.imperial_net.inventioryApp.users.service.UserService;
import com.imperial_net.inventioryApp.users.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    /**
     * Endpoint para iniciar sesión. Autentica al usuario, genera un JWT
     * y lo almacena en una cookie.
     *
     * @param request  credenciales de autenticación
     * @param response objeto HttpServletResponse donde se setea la cookie
     * @return UserDTO del usuario autenticado o error en caso de falla
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        try {
            AuthenticationResponse authResponse = authenticationService.login(request);
            ResponseCookie cookie = cookieService.createAuthCookie(authResponse.getJwt());
            response.addHeader("Set-Cookie", cookie.toString());
            UserDTO userDto = userService.toDTO(authenticationService.getUserByEmail(request.getEmail()).get());
            return ResponseEntity.ok(userDto);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (DisabledUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al conectarse al servidor."));
        }
    }

    /**
     * Endpoint para cerrar sesión. Elimina la cookie con el JWT.
     *
     * @param response objeto HttpServletResponse donde se elimina la cookie
     * @return mensaje de confirmación
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout exitoso");
    }

    /**
     * Endpoint para obtener información de la sesión actual.
     *
     * @param jwtToken token JWT extraído de la cookie
     * @param request  petición HTTP que contiene la cookie
     * @return información del usuario autenticado o mensaje de error
     */
    @GetMapping("/me")
    public ResponseEntity<?> getSession(@CookieValue(name = "authToken", required = false) String jwtToken, HttpServletRequest request) {
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
        }

        try {
            String username = jwtService.extractUserName(jwtToken);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión inválida");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserDTO user = userService.getUserSession(request);

            Map<String, Object> response = new HashMap<>();
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities());
            response.put("Name", user.getFirstName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión inválida");
        }
    }

    /**
     * Endpoint para solicitar el token de recuperación de contraseña.
     *
     * @param request objeto con el email del usuario
     * @return mensaje de confirmación
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
        userService.sendResetToken(request.getEmail());
        return ResponseEntity.ok("Si el correo está registrado, se envió el enlace.");
    }

    /**
     * Endpoint para restablecer la contraseña usando un token.
     *
     * @param request objeto con el token y la nueva contraseña
     * @return mensaje de confirmación
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada con éxito.");
    }
}