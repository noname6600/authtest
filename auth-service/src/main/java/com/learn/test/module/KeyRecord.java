package com.learn.test.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;

@Data
@Builder
public class KeyRecord {
    private String kid;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Instant createdAt;
    private boolean primary;
    private Instant expiredAt;

    public boolean isExpired() {
        return !primary && Instant.now().isAfter(expiredAt);
    }
}
