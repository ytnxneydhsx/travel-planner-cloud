package org.example.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.common.web.ApiResponse;
import org.example.common.web.RequestHeaderNames;
import org.example.userservice.dto.UserLoginRequest;
import org.example.userservice.dto.UserLoginResponse;
import org.example.userservice.dto.UserRegisterRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.success("User registered successfully.", userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success("User login successful.", userService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId) {
        UserResponse response = userService.getById(currentUserId);
        if (response == null) {
            throw new ResponseStatusException(NOT_FOUND, "User not found.");
        }
        return ApiResponse.success(response);
    }
}
