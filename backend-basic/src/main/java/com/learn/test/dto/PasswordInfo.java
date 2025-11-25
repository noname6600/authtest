package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PasswordInfo {
    private String oldPassword;
    private String newPassword;
}
