package com.nracademy.backend.mapper;

import com.nracademy.backend.dto.response.AuthUserResponse;
import com.nracademy.backend.dto.response.UserDto;
import com.nracademy.backend.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    AuthUserResponse toAuthUser(User user);

}