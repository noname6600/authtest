package com.learn.test.service.impl;

import com.learn.test.dto.AuthInfo;
import com.learn.test.dto.ForgetInfo;
import com.learn.test.dto.LoginInfo;
import com.learn.test.dto.ResetPasswordInfo;
import com.learn.test.entity.Account;
import com.learn.test.entity.User;
import com.learn.test.service.IAccountService;
import com.learn.test.service.IAuthService;
import com.learn.test.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IAccountService accountService;
    private final IUserService userService;

    public LoginInfo login(AuthInfo request) {
        Account account = accountService.verifyLogin(request.getEmail(), request.getPassword());
        User user = account.getUser();
        String credentials = request.getEmail() + ":" + request.getPassword();
        String base64Token = Base64.getEncoder().encodeToString(credentials.getBytes());
        return LoginInfo.builder()
                .name(user.getName())
                .user_id(user.getId())
                .token(base64Token)
                .build();
    }

    @Transactional
    public String register(AuthInfo request) {
        User user = userService.createUser(request.getEmail());
        accountService.createAccount(user, request.getEmail(), request.getPassword());
        return "Register ok";
    }

    @Override
    public String forgetPassword(ForgetInfo request) {
        return accountService.forgetPassword(request.getEmail());
    }

    @Override
    public String resetPassword(ResetPasswordInfo request) {
        return accountService.resetPassword(request.getToken(),request.getNewPassword());
    }
}
