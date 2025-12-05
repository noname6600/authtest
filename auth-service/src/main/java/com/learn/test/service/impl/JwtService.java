package com.learn.test.service.impl;

import com.learn.test.entity.User;
import com.learn.test.module.KeyRecord;
import com.learn.test.service.IJwtService;
import com.learn.test.service.IKeyManager;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.*;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final IKeyManager keyManager;


    @Override
    public String generateToken(User user) {
        KeyRecord key = keyManager.getCurrentKey();

        Instant now = Instant.now();
        Instant exp = now.plusMillis(keyManager.getJwtExpirationMs());

        return Jwts.builder()
                .setSubject(user.getAccount().getEmail())
                .claim("uid", user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setHeaderParam("kid", key.getKid())
                .signWith(key.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String getUserNameFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                    @Override
                    public Key resolveSigningKey(JwsHeader header, Claims claims) {
                        String kid = header.getKeyId();
                        KeyRecord key = keyManager.getByKid(kid);

                        if (key == null) {
                            throw new JwtException("Unknown KID: " + kid);
                        }

                        return key.getPublicKey();
                    }
                })
                .build()
                .parseClaimsJws(token);
    }
}

