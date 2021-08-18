package com.acme.rfdata.lang.mapper;

import com.acme.rfdata.country.Country;
import com.acme.rfdata.lang.Lang;
import com.acme.rfdata.lang.dto.LangDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LangMapper {
    LangDto toDto(Lang s);
}
