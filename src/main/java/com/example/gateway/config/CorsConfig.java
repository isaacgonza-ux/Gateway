package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Orígenes permitidos
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",  // Vite/React
                "http://localhost:3000",  // React/Next.js
                "http://localhost:4200"   // Angular
        ));
        
        // Métodos HTTP permitidos
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos
        corsConfig.setAllowedHeaders(List.of("*"));
        
        // Permitir credenciales (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);
        
        // Tiempo de caché del preflight
        corsConfig.setMaxAge(3600L);
        
        // Headers expuestos al cliente
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",
                "X-Page",
                "X-Per-Page"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}