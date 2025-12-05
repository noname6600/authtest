package com.learn.test.scheduler;

import com.learn.test.service.IKeyManager;
import com.learn.test.service.impl.KeyManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyRotationScheduler {

    private final IKeyManager keyManager;
    private final ThreadPoolTaskScheduler taskScheduler;

    @PostConstruct
    public void init() {
        Duration interval = Duration.ofMillis(keyManager.getRotationMs());

        taskScheduler.scheduleAtFixedRate(() -> {
            try {
                log.info("[KeyRotationScheduler] Rotate key triggered");
                keyManager.rotate();
            } catch (Exception e) {
                log.error("[KeyRotationScheduler] rotation failed", e);
            }
        }, interval);
    }
    @Scheduled(fixedDelay = 60_000)
    public void cleanupOldKeys() {
        keyManager.cleanupOldKeys();
    }
}