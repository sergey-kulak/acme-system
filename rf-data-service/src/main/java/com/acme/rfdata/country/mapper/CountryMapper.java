package com.acme.rfdata.country.mapper;

import com.acme.rfdata.country.Country;
import com.acme.rfdata.country.dto.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto toDto(Country s);
}
