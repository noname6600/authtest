package com.learn.test.service.impl;

import com.learn.test.dto.UserInfo;
import com.learn.test.entity.User;
import com.learn.test.exception.ResourceNotFoundException;
import com.learn.test.repository.UserRepository;
import com.learn.test.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(String name) {
        User user = User.builder()
                .name(name)
                .build();
        return userRepository.save(user);
    }

    @Override
    public UserInfo updateMe(User user, UserInfo request) {
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        return UserInfo.builder()
                .address(user.getAddress())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public UserInfo getUserInfo(User user) {
        return UserInfo.builder()
                .name(user.getName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}

