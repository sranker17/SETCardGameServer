package com.setcardgameserver.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String userName;
    private String password;
}