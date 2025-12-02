package com.learn.test.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_num")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

}
