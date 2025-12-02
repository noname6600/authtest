package com.learn.test.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String name;
    private String address;
    private String phoneNumber;
}
