package com.setcardgameserver.controller;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.LoginResponse;
import com.setcardgameserver.model.dto.LoginUserDto;
import com.setcardgameserver.model.dto.RegisterUserDto;
import com.setcardgameserver.service.AuthenticationService;
import com.setcardgameserver.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterUserDto registerUserDto) {
        return authenticationService.signup(registerUserDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        return new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
    }
}
