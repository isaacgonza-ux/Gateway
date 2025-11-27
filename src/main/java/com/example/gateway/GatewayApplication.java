package com.example.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 API Gateway iniciado en puerto 8080");
        System.out.println("📡 Enrutando a:");
        System.out.println("   Auth:     http://localhost:8081");
        System.out.println("   Products: http://localhost:8082");
        System.out.println("   Orders:   http://localhost:8083");
        System.out.println("   Payments: http://localhost:8084");
        System.out.println("   Admin:    http://localhost:8085");
        System.out.println("========================================");
    }
}