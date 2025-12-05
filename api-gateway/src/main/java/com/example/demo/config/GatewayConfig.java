package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtGatewayFilter jwtGatewayFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://auth-service"))

                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtGatewayFilter))
                        .uri("lb://auth-service"))

                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .filters(f -> f.filter(jwtGatewayFilter))
                        .uri("lb://order-service"))

                .build();
    }
}
