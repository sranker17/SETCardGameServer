package com.setcardgameserver.service;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: %s".formatted(username)));
    }

    public List<UserDto> allUsers() {
        log.info("Finding all users");
        return userMapper.entityListToDtoList(userRepository.findAll());
    }

    public UserDto getOwnUser() {
        log.info("Finding own user");
        return userMapper.entityToDto(getLoggedInUser());
    }

    public User createAdministrator(AuthUserDto input) {
        log.info("Creating administrator: {}", input.getUsername());
        Role optionalRole = roleRepository.findByName(RoleEnum.ADMIN)
                .orElseThrow();

        User user = new User()
                .setUsername(input.getUsername())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole);

        return userRepository.save(user);
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public void validateUser(String username) {
        log.info("Validating user: {}", username);
        if (!getLoggedInUser().getUsername().equals(username))
            throw new AccessDeniedException("User is not authorized to perform this action");
    }
}
