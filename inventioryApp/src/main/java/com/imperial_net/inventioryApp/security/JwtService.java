package com.imperial_net.inventioryApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase encargada de la creación, validación y extracción de datos de los JWTs (JSON Web Tokens).
 * Este servicio permite generar un token JWT, validar su autenticidad y extraer los datos del token.
 */
@Service
public class JwtService {

    // Clave secreta para firmar el JWT, que se obtiene de las propiedades de configuración.
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param jwt el token JWT.
     * @return el nombre de usuario extraído del token.
     */
    public String extractUserName(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    /**
     * Genera un nuevo token JWT a partir de los detalles del usuario.
     *
     * @param userDetails detalles del usuario.
     * @return el token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Verifica si el token JWT es válido.
     *
     * @param jwt el token JWT.
     * @param userDetails detalles del usuario.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUserName(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param jwt el token JWT.
     * @return true si el token ha expirado, false si no.
     */
    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param jwt el token JWT.
     * @return la fecha de expiración.
     */
    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    /**
     * Genera un nuevo token JWT con datos adicionales y los detalles del usuario.
     *
     * @param claimsAdd datos adicionales para incluir en el token.
     * @param userDetails detalles del usuario.
     * @return el token JWT generado.
     */
    public String generateToken(Map<String, Object> claimsAdd, UserDetails userDetails) {
        claimsAdd.put("role", userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null));

        return Jwts.builder()
                .setClaims(claimsAdd)  // Asigna los datos adicionales al token.
                .setSubject(userDetails.getUsername())  // Establece el nombre de usuario como sujeto del token.
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Establece la fecha de emisión.
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // Establece la fecha de expiración (1 hora).
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Firma el token con la clave secreta y el algoritmo HS256.
                .compact();  // Genera el token JWT.
    }

    /**
     * Extrae un reclamo específico del token JWT.
     *
     * @param jwt el token JWT.
     * @param claimsTFunction la función que se utilizará para extraer el reclamo.
     * @param <T> el tipo del valor del reclamo.
     * @return el valor del reclamo extraído.
     */
    public <T> T extractClaim(String jwt, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(jwt);
        return claimsTFunction.apply(claims);
    }

    /**
     * Extrae todos los reclamos (claims) del token JWT.
     *
     * @param jwt el token JWT.
     * @return los reclamos extraídos del token.
     */
    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())  // Establece la clave secreta para verificar la firma.
                .build()
                .parseClaimsJws(jwt)  // Analiza el token JWT.
                .getBody();  // Devuelve el cuerpo del token, que contiene los reclamos.
    }

    /**
     * Obtiene la clave secreta utilizada para firmar el JWT.
     *
     * @return la clave secreta.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Decodifica la clave secreta desde base64.
        return Keys.hmacShaKeyFor(keyBytes);  // Crea la clave de firma con el algoritmo HMAC.
    }
}
