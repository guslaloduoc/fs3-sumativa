package com.sumativa.ms_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway para microservicios
 * Utiliza Spring Cloud Gateway para enrutamiento y JWT para autenticación
 */
@SpringBootApplication
public class MsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayApplication.class, args);
    }
}
