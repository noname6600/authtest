package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginInfo {
    private String token;
    private String name;
    private Long user_id;
}
