package com.learn.test.service;

import com.learn.test.entity.Account;
import com.learn.test.entity.User;

public interface IAccountService {
    void createAccount(User user, String email, String rawPassword);
    Account verifyLogin(String email, String rawPassword);
    String forgetPassword(String email);
    void changePassword(Account account, String oldPassword, String newPassword);
    String resetPassword(String token, String newPassword);
}
