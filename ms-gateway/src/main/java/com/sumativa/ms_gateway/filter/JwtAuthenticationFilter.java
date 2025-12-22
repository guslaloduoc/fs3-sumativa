package com.sumativa.ms_gateway.filter;

import com.sumativa.ms_gateway.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtro global de autenticación JWT para Spring Cloud Gateway
 * Valida tokens JWT en todas las peticiones excepto en endpoints públicos
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    // Rutas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/users/login",
        "/api/users/register",
        "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Permitir peticiones OPTIONS (preflight CORS)
        if (request.getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            log.debug("Permitiendo petición OPTIONS (preflight CORS) a: {}", path);
            return chain.filter(exchange);
        }

        // Permitir acceso sin autenticación a rutas públicas
        if (isPublicPath(path)) {
            log.debug("Acceso público permitido a: {}", path);
            return chain.filter(exchange);
        }

        // Extraer token JWT del header Authorization
        String token = extractJwtFromRequest(request);

        if (!StringUtils.hasText(token)) {
            log.warn("Token JWT no encontrado para ruta protegida: {}", path);
            return onError(exchange, "Token de autenticación requerido", HttpStatus.UNAUTHORIZED);
        }

        // Validar token
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Token JWT inválido para ruta: {}", path);
            return onError(exchange, "Token de autenticación inválido o expirado", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Extraer información del token y agregarla a los headers para los microservicios
            String email = jwtTokenProvider.getEmailFromToken(token);
            List<String> roles = jwtTokenProvider.getRolesFromToken(token);

            log.debug("Usuario autenticado: {} con roles: {}", email, roles);

            // Agregar headers personalizados para que los microservicios puedan acceder a la información del usuario
            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Roles", String.join(",", roles))
                .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            log.error("Error al procesar token JWT: {}", e.getMessage());
            return onError(exchange, "Error al procesar autenticación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifica si la ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String extractJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Maneja errores de autenticación
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // Agregar headers CORS para que el navegador pueda leer la respuesta de error
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "*");
        log.error("Error de autenticación: {} - Status: {}", message, httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 1; // Ejecutarse después del filtro CORS (CorsWebFilter tiene orden -1)
    }
}
