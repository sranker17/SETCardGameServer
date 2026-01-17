package com.setcardgameserver.repository;

import com.setcardgameserver.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NotNull User, @NotNull Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
