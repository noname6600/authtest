package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtVerifyFilter jwtVerifyFilter) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users/**")
                        .uri("lb://auth-service"))

                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .filters(f -> f.filter(jwtVerifyFilter))
                        .uri("lb://order-service"))
                .build();
    }
}