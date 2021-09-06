package com.acme.accountingsrv.plan.mapper;

import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.commons.mapper.StringMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface PlanMapper {
    @Mapping(target = "currency", source = "currency", qualifiedByName = {"String", "toUppercase"})
    Plan fromDto(SavePlanDto source);

    PlanDto toDto(Plan source);

    @Mapping(source = "countries", target = "countries")
    PlanWithCountriesDto toDto(Plan s, Collection<String> countries);

    @Mapping(source = "countries", target = "countries")
    @Mapping(source = "publicPointCount", target = "publicPointCount")
    PlanWithCountDto toDtoWithCount(Plan source, Collection<String> countries, long publicPointCount);

    @Mapping(target = "currency", source = "currency", qualifiedByName = {"String", "toUppercase"})
    void update(@MappingTarget Plan target, SavePlanDto source);
}
