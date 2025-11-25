package com.learn.test.controller;

import com.learn.test.dto.*;
import com.learn.test.entity.User;
import com.learn.test.service.IAuthService;
import com.learn.test.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginInfo>> login(@RequestBody AuthInfo request) {

        LoginInfo result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody AuthInfo request) {
        String result = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgetPassword(@RequestBody ForgetInfo request) {
        String result = authService.forgetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordInfo request) {

        String result = authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
