package com.example.gateway.filter;

//Este filtro verifica que el usuario tenga rol de administrador antes de permitir el acceso a rutas protegidas del API Gateway. 
// Lee el header X-User-Role (que fue agregado previamente por el AuthenticationFilter al validar el JWT) y 
// comprueba que su valor sea exactamente "ADMIN". Si el rol no existe o es diferente, rechaza la petición 
// con un error 403 Forbidden; si el usuario es administrador, permite que la petición continúe hacia el microservicio correspondiente. 
// Es una capa adicional de seguridad basada en roles que se aplica después de la autenticación para proteger endpoints administrativos.

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// Filtro para autorización de administradores
@Component
public class AdminAuthorizationFilter extends AbstractGatewayFilterFactory<AdminAuthorizationFilter.Config> {

    // Constructor
    public AdminAuthorizationFilter() {
        super(Config.class);
    }
// Implementación del filtro
    @Override
    public GatewayFilter apply(Config config) { // Define la lógica del filtro
        return (exchange, chain) -> { // returnamos un GatewayFilter que procesa la petición
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role"); // Obtener el rol del header
            
            System.out.println("🔵 Gateway → Validando rol ADMIN");
            System.out.println("   Role recibido: " + role);

            if (role == null || !role.equals("ADMIN")) { // Si no es ADMIN
                System.out.println("❌ Gateway → Acceso denegado (no es ADMIN)");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403 Forbidden
                return exchange.getResponse().setComplete(); // Terminamos la respuesta
            }

            System.out.println("✅ Gateway → Acceso permitido (ADMIN)");
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuración opcional
    }
}

