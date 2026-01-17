package com.setcardgameserver.mapper;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto entityToDto(User user);

    List<UserDto> entityListToDtoList(List<User> userList);
}
