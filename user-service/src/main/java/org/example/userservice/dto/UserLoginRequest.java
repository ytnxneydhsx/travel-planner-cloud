package org.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    @NotBlank(message = "username must not be blank.")
    @Size(min = 4, max = 50, message = "username length must be between 4 and 50.")
    private String username;

    @NotBlank(message = "password must not be blank.")
    @Size(min = 6, max = 50, message = "password length must be between 6 and 50.")
    private String password;
}
