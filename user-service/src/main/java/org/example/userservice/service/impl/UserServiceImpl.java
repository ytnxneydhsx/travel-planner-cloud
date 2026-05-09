package org.example.userservice.service.impl;

import org.example.common.web.BusinessException;
import org.example.userservice.dto.UserLoginRequest;
import org.example.userservice.dto.UserLoginResponse;
import org.example.userservice.dto.UserRegisterRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.entity.User;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.service.UserService;
import org.example.userservice.support.jwt.JwtTokenProvider;
import org.example.userservice.support.password.PasswordEncoderSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoderSupport passwordEncoderSupport;

    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(
            UserMapper userMapper,
            PasswordEncoderSupport passwordEncoderSupport,
            JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoderSupport = passwordEncoderSupport;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "Username already exists.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoderSupport.encode(request.getPassword()))
                .nickname(request.getNickname())
                .status(request.getStatus() == null ? 1 : request.getStatus())
                .build();

        userMapper.insert(user);
        return toResponse(userMapper.selectById(user.getId()));
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoderSupport.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Username or password is invalid.");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User is disabled.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpireSeconds())
                .user(toResponse(user))
                .build();
    }

    @Override
    public UserResponse getById(Long id) {
        return toResponse(userMapper.selectById(id));
    }

    private UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
