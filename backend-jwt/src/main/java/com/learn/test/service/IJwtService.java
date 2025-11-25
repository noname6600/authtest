package com.learn.test.service;

import java.security.Key;

public interface IJwtService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);

}
