package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    // Configuración adicional de rutas programáticamente (opcional)
    // La mayoría de la configuración está en application.yml
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta de fallback para circuit breaker
                .route("fallback", r -> r
                        .path("/fallback")
                        .uri("forward:/fallback"))
                .build();
    }
}