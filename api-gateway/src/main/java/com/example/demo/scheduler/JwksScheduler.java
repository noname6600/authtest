package com.example.demo.scheduler;

import com.example.demo.config.JwksProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class JwksScheduler {

    private static final Logger log = LoggerFactory.getLogger(JwksScheduler.class);

    private final JwksProvider jwksProvider;


    @Scheduled(fixedDelay = 30_000)
    public void refreshJwks() {
        try {
            jwksProvider.fetchJwksSync();
        } catch (Exception ex) {
            log.error("[JWKS Scheduler] fetch failed", ex);
        }
    }


    @Scheduled(fixedDelay = 30_000)
    public void cleanupOldKeys() {
        try {
            jwksProvider.cleanupOldKeys();
        } catch (Exception ex) {
            log.error("[JWKS Scheduler] cleanup failed", ex);
        }
    }
}
