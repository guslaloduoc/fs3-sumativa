package com.sumativa.ms_laboratorios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS para permitir peticiones desde el frontend Angular.
 *
 * Esta configuración permite:
 * - Peticiones desde http://localhost:4200 (Angular dev server)
 * - Todos los métodos HTTP (GET, POST, PUT, DELETE, PATCH)
 * - Headers personalizados necesarios para la aplicación
 *
 * IMPORTANTE: En producción, ajustar allowedOrigins con la URL real del frontend
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:4200",  // Angular dev server
                    "http://127.0.0.1:4200"   // Alternativa
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache de preflight requests por 1 hora
    }
}
