package com.setcardgameserver.service;

import com.setcardgameserver.model.Role;
import com.setcardgameserver.model.RoleEnum;
import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.LoginUserDto;
import com.setcardgameserver.model.dto.RegisterUserDto;
import com.setcardgameserver.repository.RoleRepository;
import com.setcardgameserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User signup(RegisterUserDto input) {
        log.info("Creating user: {}", input.getUsername());
        Role optionalRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow();

        User user = new User()
                .setUsername(input.getUsername())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        log.info("Authenticating user: {}", input.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}