package com.sumativa.ms_usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración Web para el microservicio de usuarios.
 *
 * NOTA: CORS está deshabilitado en los microservicios.
 * El API Gateway (ms-gateway) maneja toda la configuración CORS
 * para evitar headers duplicados/conflictivos.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS manejado por el Gateway
}
