package com.learn.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "auth_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String kid;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String privateKey;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String publicKey;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean primaryKey;

    @Column(nullable = false)
    private Instant expiredAt;
}
