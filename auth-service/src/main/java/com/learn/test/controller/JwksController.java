package com.learn.test.controller;


import com.learn.test.service.impl.KeyManager;
import com.learn.test.utils.JwkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final KeyManager keyManager;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        List<Map<String, Object>> jwkList = keyManager.getAllKeysForJwks()
                .stream()
                .map(JwkUtils::toJwk)
                .toList();
        return Map.of("keys", jwkList);
    }
}

