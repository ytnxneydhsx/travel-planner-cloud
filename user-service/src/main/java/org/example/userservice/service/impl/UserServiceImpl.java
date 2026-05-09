package org.example.userservice.service.impl;

import org.example.common.web.BusinessException;
import org.example.userservice.constant.UserConstants;
import org.example.userservice.dto.UserLoginRequest;
import org.example.userservice.dto.UserLoginResponse;
import org.example.userservice.dto.UserRegisterRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.entity.User;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.service.UserService;
import org.example.userservice.support.jwt.JwtTokenProvider;
import org.example.userservice.support.password.PasswordEncoderSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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
            log.warn("User registration rejected because username already exists: {}", request.getUsername());
            throw new BusinessException(HttpStatus.CONFLICT, "Username already exists.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoderSupport.encode(request.getPassword()))
                .nickname(request.getNickname())
                .status(UserConstants.resolveStatusOrDefault(request.getStatus()))
                .build();

        userMapper.insert(user);
        return toResponse(userMapper.selectById(user.getId()));
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoderSupport.matches(request.getPassword(), user.getPassword())) {
            log.warn("User login rejected due to invalid credentials: {}", request.getUsername());
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Username or password is invalid.");
        }
        if (UserConstants.isDisabledStatus(user.getStatus())) {
            log.warn("User login rejected because account is disabled: userId={}", user.getId());
            throw new BusinessException(HttpStatus.FORBIDDEN, "User is disabled.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .tokenType(UserConstants.TOKEN_TYPE_BEARER)
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
                .createdAt(user.getGmtCreate())
                .updatedAt(user.getGmtModified())
                .build();
    }
}
