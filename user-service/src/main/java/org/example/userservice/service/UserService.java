package org.example.userservice.service;

import org.example.userservice.dto.UserLoginRequest;
import org.example.userservice.dto.UserLoginResponse;
import org.example.userservice.dto.UserRegisterRequest;
import org.example.userservice.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

    UserResponse getById(Long id);
}
