package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordInfo {
    private String token;
    private String newPassword;
}
