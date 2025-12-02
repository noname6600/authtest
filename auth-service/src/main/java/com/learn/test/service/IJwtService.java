package com.learn.test.service;

import com.learn.test.entity.User;

import java.security.Key;

public interface IJwtService {
    String generateToken(User user);
    String getUserNameFromToken(String token);
    boolean validateToken(String token);
}
