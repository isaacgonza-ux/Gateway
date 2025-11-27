package com.example.gateway.filter;



import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        // Log de la petición entrante
        System.out.println("========================================");
        System.out.println("🌐 [" + LocalDateTime.now().format(formatter) + "] INCOMING REQUEST");
        System.out.println("   Method: " + request.getMethod());
        System.out.println("   Path: " + request.getPath());
        System.out.println("   Remote Address: " + request.getRemoteAddress());
        
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("   Token: Present (" + authHeader.substring(7, Math.min(27, authHeader.length())) + "...)");
        } else {
            System.out.println("   Token: None");
        }

        // Continuar con la cadena de filtros
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log de la respuesta
            System.out.println("📤 [" + LocalDateTime.now().format(formatter) + "] OUTGOING RESPONSE");
            System.out.println("   Status: " + response.getStatusCode());
            System.out.println("   Duration: " + duration + "ms");
            System.out.println("========================================");
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Se ejecuta primero
    }
}