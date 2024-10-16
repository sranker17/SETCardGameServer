package com.setcardgameserver.repository;

import com.setcardgameserver.model.Role;
import com.setcardgameserver.model.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
