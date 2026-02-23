package com.example.gateway.filter;

//Este filtro actúa como guardián de seguridad en el API Gateway, interceptando todas las peticiones antes de que lleguen a 
// los microservicios. Verifica que cada petición tenga un token JWT válido en el header Authorization, 
// lo decodifica usando la clave secreta, y valida que no esté expirado o corrupto. Si el token es válido,
//  extrae información del usuario (ID, email, rol, username) y la agrega como headers personalizados 
// (X-User-Id, X-User-Email, etc.) a la petición antes de enviarla a los microservicios, 
// permitiendo que estos sepan quién está haciendo la petición sin tener que validar el token nuevamente. 
// Si el token falta, está mal formateado, expiró o es inválido, rechaza la petición con un error 401 Unauthorized. 
// Es como un punto de control centralizado que valida la identidad del usuario una sola vez en el gateway antes de distribuir las peticiones.

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    // Clave secreta para validar el JWT
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Constructor
    public AuthenticationFilter() { // Llama al constructor padre con la clase de configuración
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) { // Define la lógica del filtro
        return (exchange, chain) -> { // returnamos un GatewayFilter que procesa la petición
            ServerHttpRequest request = exchange.getRequest(); // Obtenemos la petición entrante

            //Deja pasar las peticiones OPTIONS del navegador sin pedir token
            if (request.getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }
            
            System.out.println("🔵 Gateway → Validando autenticación");
            System.out.println("   Path: " + request.getPath());

            // Verificar si tiene header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) { // No hay token
                System.out.println("❌ Gateway → No hay token");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
                return exchange.getResponse().setComplete(); // Terminamos la respuesta
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION); // Obtener el token

            if (authHeader == null || !authHeader.startsWith("Bearer ")) { // si es nulo o no empieza con Bearer
                System.out.println("❌ Gateway → Token mal formateado");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
                return exchange.getResponse().setComplete(); // Terminamos la respuesta
            }

            String token = authHeader.substring(7).trim(); // Extraer el token (remover "Bearer ")
 
            try {
                // Validar y extraer claims del JWT
                Claims claims = Jwts.parserBuilder()
                       .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret.trim()))) // Usar la clave secreta
                        .build() // Construir el parser
                        .parseClaimsJws(token) // Parsear el token
                        .getBody(); // Obtener el cuerpo (claims)

                System.out.println("✅ Gateway → Token válido");
                System.out.println("   User: " + claims.getSubject());
                System.out.println("   Role: " + claims.get("role"));

                // Agregar información del usuario a los headers
                // para que los microservicios la reciban
                ServerHttpRequest modifiedRequest = request.mutate() // Crear una petición modificada
                        .header("X-User-Id", String.valueOf(claims.get("userId"))) // Agregar ID de usuario
                        .header("X-User-Email", String.valueOf(claims.get("email"))) // Agregar email de usuario
                        .header("X-User-Role", String.valueOf(claims.get("role"))) // Agregar rol de usuario
                        .header("X-Username", claims.getSubject()) // Agregar username (subject)
                        .build();

                        // Crear un nuevo exchange con la petición modificada
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                        // Continuar con la cadena de filtros con la petición modificada
                return chain.filter(modifiedExchange);

            } catch (io.jsonwebtoken.ExpiredJwtException e) { // Token expirado
                System.out.println("❌ Gateway → Token expirado");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
                return exchange.getResponse().setComplete();
            } catch (Exception e) {
                System.out.println("❌ Gateway → Token inválido: " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        // Configuración opcional del filtro
    }
}