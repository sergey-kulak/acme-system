package com.acme.commons.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Named("String")
@Mapper(componentModel = "spring")
public class StringMapper {

    @Named("toUppercase")
    public String toUppercase(String value) {
        return StringUtils.upperCase(value);
    }

    @Named("toLowercase")
    public String toLowercase(String value) {
        return StringUtils.lowerCase(value);
    }
}
