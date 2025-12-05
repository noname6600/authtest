package com.example.demo.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import net.minidev.json.JSONObject;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.nimbusds.jose.jwk.*;



@Component
@RequiredArgsConstructor
@Slf4j
public class JwksProvider {


    private final WebClient.Builder webClient;

    private final Map<String, RSAKey> cache = new ConcurrentHashMap<>();
    private final Map<String, Instant> cacheExpirations = new ConcurrentHashMap<>();

    private final Set<String> invalidKids = ConcurrentHashMap.newKeySet();
    private final Map<String, Long> invalidKidTimestamps = new ConcurrentHashMap<>();

    @Value("${auth.jwks-uri}")
    private String jwksUri;

    private final long cacheTtlMs = 30_000;
    private final long invalidKidTtlMs = 60_000;
    private final int maxInvalidKids = 1000;

    private volatile long lastFetch = 0;


    public RSAKey getKey(String kid) {
        long nowMs = System.currentTimeMillis();

        invalidKidTimestamps.entrySet().removeIf(e -> nowMs - e.getValue() > invalidKidTtlMs);
        invalidKids.removeIf(k -> !invalidKidTimestamps.containsKey(k));

        if (invalidKids.contains(kid)) return null;

        RSAKey key = cache.get(kid);
        if (key != null) return key;

        boolean fetchAllowed = nowMs - lastFetch > cacheTtlMs;
        if (fetchAllowed) {
            synchronized (this) {
                if (nowMs - lastFetch > cacheTtlMs) {
                    try {
                        fetchJwksSync();
                    } catch (Exception ex) {
                        log.error("[JWKS] Fetch failed", ex);
                    } finally {
                        lastFetch = nowMs;
                    }
                }
            }
        }

        key = cache.get(kid);
        if (key == null && invalidKids.size() < maxInvalidKids) {
            invalidKids.add(kid);
            invalidKidTimestamps.put(kid, nowMs);
        }

        return key;
    }

    public void fetchJwksSync() throws Exception {
        log.info("[JWKS] Fetching JWKS from: {}", jwksUri);

        String json = webClient.build()
                .get()
                .uri(jwksUri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null) throw new RuntimeException("Empty JWKS response");

        JWKSet set = JWKSet.parse(json);
        Instant now = Instant.now();

        for (JWK key : set.getKeys()) {
            if (key instanceof RSAKey rsa) {
                Instant exp = null;


                Map<String, Object> keyMap = key.toJSONObject();
                Object expObj = keyMap.get("exp");

                if (expObj instanceof Number n) {
                    exp = Instant.ofEpochMilli(n.longValue());
                } else if (expObj instanceof String s) {
                    try {
                        exp = Instant.ofEpochMilli(Long.parseLong(s));
                    } catch (NumberFormatException ignored) {}
                }

                if (exp == null) exp = now.plusMillis(cacheTtlMs);

                cache.put(rsa.getKeyID(), rsa);
                cacheExpirations.put(rsa.getKeyID(), exp);
                invalidKids.remove(rsa.getKeyID());
                invalidKidTimestamps.remove(rsa.getKeyID());
            }
        }

        log.info("[JWKS] Cache updated: {} keys", cache.size());
    }

    public void cleanupOldKeys() {
        Instant now = Instant.now();

        cache.entrySet().removeIf(entry -> {
            Instant exp = cacheExpirations.get(entry.getKey());
            if (exp != null && exp.isBefore(now)) {
                cacheExpirations.remove(entry.getKey());
                invalidKids.remove(entry.getKey());
                invalidKidTimestamps.remove(entry.getKey());
                log.info("[JWKS] Removed expired key from cache: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }
}