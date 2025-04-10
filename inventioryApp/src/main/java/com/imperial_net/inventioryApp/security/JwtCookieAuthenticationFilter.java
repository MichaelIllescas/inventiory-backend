package com.imperial_net.inventioryApp.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que intercepta las solicitudes HTTP para verificar el token JWT en las cookies.
 * Si el token es válido, autentica al usuario.
 */
@Component
@RequiredArgsConstructor
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    // Servicio encargado de manejar operaciones relacionadas con JWT.
    private final JwtService jwtService;
    // Servicio de detalles de usuario para cargar la información del usuario.
    private final UserDetailsService userDetailsService;

    /**
     * Método principal del filtro, intercepta las solicitudes HTTP y valida el token JWT.
     * Si el token es válido, establece la autenticación del usuario en el contexto de seguridad.
     *
     * @param request  la solicitud HTTP entrante.
     * @param response la respuesta HTTP saliente.
     * @param filterChain la cadena de filtros.
     * @throws java.io.IOException si ocurre un error en el manejo de la respuesta.
     * @throws jakarta.servlet.ServletException si ocurre un error en el procesamiento de la solicitud.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {

        try {
            // Extrae el token JWT de las cookies de la solicitud
            String jwtToken = extractTokenFromCookies(request);
            if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extrae el nombre de usuario del token JWT
                String username = jwtService.extractUserName(jwtToken);
                if (username != null) {
                    // Carga los detalles del usuario basado en el nombre de usuario
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // Si el token es válido, autentica al usuario
                    if (jwtService.isTokenValid(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // Establece la autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            // Continúa con el filtro siguiente en la cadena
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // Si el token está expirado, responde con un estado de "No autorizado"
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token expired\"}");
        }
    }

    /**
     * Extrae el token JWT desde las cookies de la solicitud HTTP.
     *
     * @param request la solicitud HTTP entrante.
     * @return el token JWT, si se encuentra; de lo contrario, devuelve null.
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        // Obtiene las cookies de la solicitud
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Recorre las cookies para encontrar el token de autenticación
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
