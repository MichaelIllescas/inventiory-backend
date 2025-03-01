package com.imperial_net.inventioryApp.services;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
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
}
