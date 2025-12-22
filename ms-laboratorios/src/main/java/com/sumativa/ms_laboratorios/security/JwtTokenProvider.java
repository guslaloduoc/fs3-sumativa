package com.sumativa.ms_laboratorios.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Componente para la generación y validación de tokens JWT
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpirationMs;

    /**
     * Valida si un token JWT es válido
     *
     * @param token Token JWT
     * @return true si es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string vacío: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Error de seguridad JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae el email (subject) del token JWT
     *
     * @param token Token JWT
     * @return Email del usuario
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extrae los roles del token JWT
     *
     * @param token Token JWT
     * @return Lista de roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * Extrae los claims del token JWT
     *
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
