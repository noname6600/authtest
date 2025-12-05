package com.learn.test.service;

import com.learn.test.module.KeyRecord;

import java.util.Collection;

public interface IKeyManager {
    long getJwtExpirationMs();
    long getRotationMs();
    KeyRecord rotate() throws Exception;
    Collection<KeyRecord> getAllKeysForJwks();
    void cleanupOldKeys();
    KeyRecord getCurrentKey();
    KeyRecord getByKid(String kid);

}
