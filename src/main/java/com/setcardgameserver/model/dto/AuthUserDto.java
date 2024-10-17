package com.setcardgameserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthUserDto {
    @NotBlank(message = "The username is required")
    private String username;
    @NotBlank(message = "The password is required")
    private String password;
}
