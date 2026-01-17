package com.setcardgameserver.controller;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.AuthUserDto;
import com.setcardgameserver.model.dto.LoginResponse;
import com.setcardgameserver.model.dto.UserDto;
import com.setcardgameserver.service.AuthenticationService;
import com.setcardgameserver.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    @Operation(summary = "Signup new user", description = "Signup new user, no authentication needed")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody AuthUserDto authUserDto) {
        return authenticationService.signup(authUserDto);
    }

    @Operation(summary = "Login user", description = "Login user, no authentication needed")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse authenticate(@Valid @RequestBody AuthUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        return new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
    }
}
