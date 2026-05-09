package org.example.userservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.userservice.constant.UserConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "username must not be blank.")
    @Size(min = 4, max = 50, message = "username length must be between 4 and 50.")
    private String username;

    @NotBlank(message = "password must not be blank.")
    @Size(min = 6, max = 50, message = "password length must be between 6 and 50.")
    private String password;

    @Size(max = 100, message = "nickname length must be less than or equal to 100.")
    private String nickname;

    @Min(value = UserConstants.USER_STATUS_DISABLED, message = "status must be 0 or 1.")
    @Max(value = UserConstants.USER_STATUS_ENABLED, message = "status must be 0 or 1.")
    private Integer status;
}
