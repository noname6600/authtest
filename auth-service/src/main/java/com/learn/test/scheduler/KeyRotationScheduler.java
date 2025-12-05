package com.learn.test.scheduler;

import com.learn.test.service.impl.KeyManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class KeyRotationScheduler {

    private final KeyManager keyManager;
    private final ThreadPoolTaskScheduler taskScheduler;

    @PostConstruct
    public void init() {
        Duration interval = Duration.ofMillis(keyManager.getRotationMs());

        taskScheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[KeyRotationScheduler] Rotate key triggered");
                keyManager.rotate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, interval);
    }
    @Scheduled(fixedDelay = 60_000)
    public void cleanupOldKeys() {
        keyManager.cleanupOldKeys();
    }
}