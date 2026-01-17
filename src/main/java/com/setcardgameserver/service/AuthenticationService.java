package com.setcardgameserver.service;

import com.setcardgameserver.exception.InvalidUsernameException;
import com.setcardgameserver.mapper.UserMapper;
import com.setcardgameserver.model.Role;
import com.setcardgameserver.model.RoleEnum;
import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.AuthUserDto;
import com.setcardgameserver.model.dto.UserDto;
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
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto signup(AuthUserDto input) {
        log.info("Creating user: {}", input.getUsername());

        if (userRepository.existsByUsername(input.getUsername())) {
            log.debug("Username is already taken {}", input.getUsername());
            throw new InvalidUsernameException("Username is already taken");
        }

        Role optionalRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow();

        User user = new User()
                .setUsername(input.getUsername())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole);

        return userMapper.entityToDto(userRepository.save(user));
    }

    public User authenticate(AuthUserDto input) {
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