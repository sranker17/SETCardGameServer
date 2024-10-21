package com.setcardgameserver.model.dto;

import com.setcardgameserver.model.RoleEnum;
import lombok.Data;

@Data
public class RoleDto {
    private RoleEnum name;
    private String description;
}
