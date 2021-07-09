package com.acme.usersrv.user.mapper;

import com.acme.usersrv.common.mapper.StringMapper;
import com.acme.usersrv.company.dto.SaveOwnerDto;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.CreateUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface UserMapper {
    CreateUserDto convert(SaveOwnerDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", source = "email", qualifiedByName = {"String", "toLowercase"})
    User fromDto(CreateUserDto dto);
}
