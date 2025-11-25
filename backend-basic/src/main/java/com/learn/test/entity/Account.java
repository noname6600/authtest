package com.learn.test.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Account extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String pass;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reset_token")
    private String resetToken;

}
