package com.example.demo.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GatewayFilter {

    private final JwksProvider jwksProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing token");
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT signed = SignedJWT.parse(token);
            String kid = signed.getHeader().getKeyID();

            if (kid == null) {
                return unauthorized(exchange, "Missing kid");
            }

            RSAKey rsa = jwksProvider.getKey(kid);
            if (rsa == null) {
                return unauthorized(exchange, "Key not found in JWKS cache");
            }

            JWSVerifier verifier = new RSASSAVerifier(rsa.toRSAPublicKey());
            if (!signed.verify(verifier)) {
                return unauthorized(exchange, "Invalid signature");
            }

            JWTClaimsSet claims = signed.getJWTClaimsSet();
            String email = claims.getSubject();
            String userId = claims.getStringClaim("uid");

            ServerHttpRequest mutated = exchange.getRequest()
                    .mutate()
                    .header("X-USER-EMAIL", email)
                    .header("X-USER-ID", userId != null ? userId : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (Exception e) {
            return unauthorized(exchange, "Invalid token format: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange ex, String msg) {
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = ex.getResponse()
                .bufferFactory()
                .wrap(msg.getBytes(StandardCharsets.UTF_8));

        return ex.getResponse().writeWith(Mono.just(buffer));
    }
}
