package com.acme.rfdatasrv.country.mapper;

import com.acme.rfdatasrv.country.Country;
import com.acme.rfdatasrv.country.dto.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto toDto(Country s);
}
