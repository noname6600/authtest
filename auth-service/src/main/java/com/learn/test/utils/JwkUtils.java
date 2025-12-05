package com.learn.test.utils;

import com.learn.test.module.KeyRecord;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

public class JwkUtils {

    public static Map<String, Object> toJwk(KeyRecord rec) {
        RSAPublicKey pub = (RSAPublicKey) rec.getPublicKey();
        long exp = rec.getExpiredAt().toEpochMilli();

        return Map.of(
                "kty", "RSA",
                "kid", rec.getKid(),
                "alg", "RS256",
                "use", "sig",
                "n", base64Url(pub.getModulus().toByteArray()),
                "e", base64Url(pub.getPublicExponent().toByteArray()),
                "exp", exp
        );
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}


