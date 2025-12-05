package com.learn.test.service.impl;

import com.learn.test.entity.KeyEntity;
import com.learn.test.exception.ResourceNotFoundException;
import com.learn.test.module.KeyRecord;
import com.learn.test.repository.KeyRepository;
import com.learn.test.service.IKeyManager;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KeyManager implements IKeyManager {

    private final KeyRepository keyRepository;
    private final long jwtExpirationMs;
    private final long rotationMs;

    private final Map<String, KeyRecord> keyStore = new ConcurrentHashMap<>();
    private String currentKid;

    public KeyManager(
            KeyRepository keyRepository,
            @Value("${key.jwt-expiration-ms}") long jwtExpirationMs,
            @Value("${key.rotation-ms}") long rotationMs) {
        this.keyRepository = keyRepository;
        this.jwtExpirationMs = jwtExpirationMs;
        this.rotationMs = rotationMs;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public long getRotationMs() {
        return rotationMs;
    }

    private long gracePeriodMs() {
        return jwtExpirationMs + rotationMs;
    }

    @PostConstruct
    public void init() throws Exception {
        List<KeyEntity> keys = keyRepository.findAll();
        Instant now = Instant.now();

        for (KeyEntity k : keys) {
            KeyRecord record = entityToRecord(k);

            if (record.getExpiredAt() == null) {
                record.setExpiredAt(record.getCreatedAt().plusMillis(gracePeriodMs()));
            }

            keyStore.put(k.getKid(), record);

            if (k.isPrimaryKey()) currentKid = k.getKid();
        }

        if (currentKid == null) rotate();
    }

    public synchronized KeyRecord rotate() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String kid = UUID.randomUUID().toString();
        Instant now = Instant.now();

        Instant expiredAt = now.plusMillis(gracePeriodMs());

        KeyRecord newKey = KeyRecord.builder()
                .kid(kid)
                .privateKey(keyPair.getPrivate())
                .publicKey(keyPair.getPublic())
                .createdAt(now)
                .primary(true)
                .expiredAt(expiredAt)
                .build();

        if (currentKid != null) {
            KeyRecord old = keyStore.get(currentKid);
            old.setPrimary(false);

            keyRepository.findByKid(currentKid)
                    .ifPresent(oldEntity -> {
                        oldEntity.setPrimaryKey(false);
                        keyRepository.save(oldEntity);
                    });
        }

        keyStore.put(kid, newKey);
        currentKid = kid;

        keyRepository.save(recordToEntity(newKey));
        return newKey;
    }

    public Collection<KeyRecord> getAllKeysForJwks() {
        Instant now = Instant.now();
        return keyStore.values().stream()
                .filter(k -> k.isPrimary() || !k.isExpired())
                .toList();
    }

    public void cleanupOldKeys() {
        List<String> toDelete = keyStore.entrySet().stream()
                .filter(e -> e.getValue().isExpired())
                .map(Map.Entry::getKey)
                .toList();

        toDelete.forEach(kid -> {
            keyStore.remove(kid);
            keyRepository.deleteById(kid);
            log.info("[KeyManager] Removed old key: " + kid);
        });
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    private KeyRecord entityToRecord(KeyEntity entity) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] privateBytes = Base64.getDecoder().decode(entity.getPrivateKey());
        byte[] publicBytes = Base64.getDecoder().decode(entity.getPublicKey());
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicBytes));
        return KeyRecord.builder()
                .kid(entity.getKid())
                .privateKey(privateKey)
                .publicKey(publicKey)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : Instant.now())
                .primary(entity.isPrimaryKey())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    private KeyEntity recordToEntity(KeyRecord record) {
        return KeyEntity.builder()
                .kid(record.getKid())
                .privateKey(Base64.getEncoder().encodeToString(record.getPrivateKey().getEncoded()))
                .publicKey(Base64.getEncoder().encodeToString(record.getPublicKey().getEncoded()))
                .createdAt(record.getCreatedAt())
                .primaryKey(record.isPrimary())
                .expiredAt(record.getExpiredAt())
                .build();
    }

    public KeyRecord getCurrentKey() {
        return keyStore.get(currentKid);
    }

    public KeyRecord getByKid(String kid) {
        return keyStore.get(kid);
    }
}


