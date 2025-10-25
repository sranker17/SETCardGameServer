package com.setcardgameserver.repository;

import com.setcardgameserver.model.Role;
import com.setcardgameserver.model.RoleEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<@NotNull Role, @NotNull Long> {
    Optional<Role> findByName(RoleEnum name);
}
