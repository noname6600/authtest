package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();

        log.info("[Gateway] Incoming request {} {}", exchange.getRequest().getMethod(), path);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - start;
                    int status = exchange.getResponse().getStatusCode().value();
                    log.info("[Gateway] {} {} → {} ({} ms)",
                            exchange.getRequest().getMethod(),
                            path,
                            status,
                            duration
                    );
                });
    }
}
