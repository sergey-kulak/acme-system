package com.acme.usersrv.company.mapper;

import com.acme.usersrv.common.mapper.StringMapper;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import com.acme.usersrv.jooq.tables.records.CompanyRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface CompanyMapper {
    @Mapping(target = "country", source = "country", qualifiedByName = {"String", "toUppercase"})
    @Mapping(target = "vatin", source = "vatin", qualifiedByName = {"String", "toUppercase"})
    Company fromDto(RegisterCompanyDto s);

    CompanyDto toDto(Company s);

    FullDetailsCompanyDto toFullDetailsDto(Company s);

    void update(@MappingTarget Company target, UpdateCompanyDto source);
}
