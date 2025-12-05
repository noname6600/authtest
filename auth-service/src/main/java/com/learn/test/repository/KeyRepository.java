package com.learn.test.repository;

import com.learn.test.entity.KeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KeyRepository extends JpaRepository<KeyEntity, String> {
        Optional<KeyEntity> findByKid(String kid);
        Optional<KeyEntity> findByPrimaryKeyTrue();
}
