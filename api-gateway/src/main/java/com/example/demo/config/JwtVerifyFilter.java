package com.example.demo.config;


import com.example.demo.dto.UserInfo;
import com.example.demo.dto.ApiResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtVerifyFilter implements GatewayFilter {

    private final WebClient webClient;

    public JwtVerifyFilter(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://auth-service").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();
        System.out.println("[GATEWAY] REQUEST PATH: " + path + " | METHOD: " + exchange.getRequest().getMethod());

        // 1️⃣ Bypass OPTIONS preflight
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        // 2️⃣ Bypass public routes (login, register, auth/**)
        if (path.startsWith("/api/v1/auth") || path.startsWith("/api/v1/register")) {
            return chain.filter(exchange);
        }

        // 3️⃣ Lấy header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println("[GATEWAY] AUTH HEADER = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[GATEWAY] NO AUTH HEADER → 401");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 4️⃣ Gọi Auth-service verify token
        return webClient.post()
                .uri("/api/v1/auth/verify")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserInfo>>() {})
                .flatMap(response -> {
                    System.out.println("[GATEWAY] VERIFY RESULT = " + response.getStatus());
                    System.out.println("[GATEWAY] USERID = " + (response.getData() != null ? response.getData().getUserId() : "null"));

                    if ("success".equals(response.getStatus()) && response.getData() != null) {
                        String userId = response.getData().getUserId();
                        if (userId == null || userId.isEmpty()) {
                            System.out.println("[GATEWAY] USERID NULL → 401");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        // 5️⃣ Thêm header X-USER-ID trước khi forward request
                        ServerHttpRequest mutatedRequest = exchange.getRequest()
                                .mutate()
                                .header("X-USER-ID", userId)
                                .build();

                        System.out.println("[GATEWAY] FORWARD TO SERVICE WITH X-USER-ID: " + userId);

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    } else {
                        System.out.println("[GATEWAY] VERIFY FAIL → 401");
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(e -> {
                    System.out.println("[GATEWAY] ERROR VERIFY TOKEN → 401 | Error = " + e);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}