package com.acme.usersrv.user.mapper;

import com.acme.commons.mapper.StringMapper;
import com.acme.usersrv.company.dto.CreateOwnerDto;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UpdateUserDto;
import com.acme.usersrv.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface UserMapper {
    CreateUserDto convert(CreateOwnerDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", source = "email", qualifiedByName = {"String", "toLowercase"})
    User fromDto(CreateUserDto dto);

    UserDto toDto(User source);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    void update(@MappingTarget User target, UpdateUserDto dto);

}
