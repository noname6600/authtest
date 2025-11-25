package com.learn.test.controller;

import com.learn.test.configuration.CustomUserDetails;
import com.learn.test.dto.ApiResponse;
import com.learn.test.dto.UserInfo;
import com.learn.test.entity.Account;
import com.learn.test.entity.User;
import com.learn.test.service.IUserService;
import com.learn.test.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getAccount().getUser();

        UserInfo info = userService.getUserInfo(user);

        return ResponseEntity.ok(info);
    }

    @PutMapping("/me")
    public ResponseEntity<UserInfo> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserInfo updateRequest) {
        User user = userDetails.getAccount().getUser();
        UserInfo updatedUser = userService.updateMe(user, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
