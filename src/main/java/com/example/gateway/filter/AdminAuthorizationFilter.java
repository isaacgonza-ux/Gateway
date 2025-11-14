package com.example.gateway.filter;



import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthorizationFilter extends AbstractGatewayFilterFactory<AdminAuthorizationFilter.Config> {

    public AdminAuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            
            System.out.println("🔵 Gateway → Validando rol ADMIN");
            System.out.println("   Role recibido: " + role);

            if (role == null || !role.equals("ADMIN")) {
                System.out.println("❌ Gateway → Acceso denegado (no es ADMIN)");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            System.out.println("✅ Gateway → Acceso permitido (ADMIN)");
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuración opcional
    }
}

