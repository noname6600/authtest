package com.learn.test.service;

import com.learn.test.dto.UserInfo;
import com.learn.test.entity.User;

public interface IUserService {
    UserInfo getUserInfo(User user);
    User createUser(String name);
    UserInfo updateMe(User user, UserInfo request);
}
