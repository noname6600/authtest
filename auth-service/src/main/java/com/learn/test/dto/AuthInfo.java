package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthInfo {
    private String email;
    private String password;
}
