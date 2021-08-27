package com.acme.rfdatasrv.lang.mapper;

import com.acme.rfdatasrv.lang.Lang;
import com.acme.rfdatasrv.lang.dto.LangDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LangMapper {
    LangDto toDto(Lang s);
}
