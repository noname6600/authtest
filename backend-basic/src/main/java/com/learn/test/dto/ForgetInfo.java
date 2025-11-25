package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgetInfo {
    private String email;
}
