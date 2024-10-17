package com.setcardgameserver.controller;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.RegisterUserDto;
import com.setcardgameserver.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
public class AdminController {
    private final UserService userService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public User createAdministrator(@RequestBody RegisterUserDto registerUserDto) {
        return userService.createAdministrator(registerUserDto);
    }
}
