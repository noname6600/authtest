package com.learn.test.service;

import com.learn.test.dto.*;
import com.learn.test.entity.User;

public interface IAuthService {
    LoginInfo login(AuthInfo request);
    String register(AuthInfo request);
    String forgetPassword(ForgetInfo request);
    String resetPassword(ResetPasswordInfo request);
}
