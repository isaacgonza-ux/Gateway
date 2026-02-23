package com.example.gateway.filter;

//Este filtro global registra y monitorea todas las peticiones HTTP que pasan por el API Gateway, 
// funcionando como un sistema de logging centralizado. Captura información de cada petición entrante 
// (método HTTP, ruta, dirección IP, presencia de token) y mide el tiempo que tarda en procesarse. 
// Una vez que la petición se completa, registra la respuesta (código de estado HTTP y duración total en milisegundos),
//  mostrando todo en consola con formato legible y timestamps. Al tener la prioridad más alta (HIGHEST_PRECEDENCE), 
// se ejecuta antes que cualquier otro filtro, permitiendo rastrear todas las operaciones del gateway para debugging,
//  monitoreo de rendimiento y auditoría de tráfico.

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

// Filtro global para logging de peticiones y respuestas
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    // Formateador de fecha y hora para los logs
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Implementación del filtro
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
        
        // Verificar si hay token en el header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) { // si hay token
            System.out.println("   Token: Present (" + authHeader.substring(7, Math.min(27, authHeader.length())) + "...)");
        } else {
            System.out.println("   Token: None"); // no hay token
        }

        // Continuar con la cadena de filtros
        return chain.filter(exchange).then(Mono.fromRunnable(() -> { 
            ServerHttpResponse response = exchange.getResponse(); // Obtener la respuesta
            long endTime = System.currentTimeMillis(); // Tiempo de finalización
            long duration = endTime - startTime; // Calcular duración

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