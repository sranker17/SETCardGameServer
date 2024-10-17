package com.setcardgameserver.bootstrap;

import com.setcardgameserver.model.Role;
import com.setcardgameserver.model.RoleEnum;
import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.AuthUserDto;
import com.setcardgameserver.repository.RoleRepository;
import com.setcardgameserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class DBSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
        this.createSuperAdministrator();
    }

    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[]{RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN};
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.SUPER_ADMIN, "Super Administrator role",
                RoleEnum.ADMIN, "Administrator role",
                RoleEnum.USER, "Default user role"
        );

        Arrays.stream(roleNames).forEach(roleName -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(role -> log.debug("Role already exists: {}", role), () -> {
                Role roleToCreate = new Role();
                roleToCreate
                        .setName(roleName)
                        .setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }

    private void createSuperAdministrator() {
        AuthUserDto userDto = new AuthUserDto();
        //TODO: Change the default super admin credentials
        userDto.setUsername("super_admin").setPassword("123456");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            log.debug("Super Admin not created");
            return;
        }

        User user = new User()
                .setUsername(userDto.getUsername())
                .setPassword(passwordEncoder.encode(userDto.getPassword()))
                .setRole(optionalRole.get());

        log.debug("Super Admin created with username: {}", user.getUsername());

        userRepository.save(user);
    }
}
