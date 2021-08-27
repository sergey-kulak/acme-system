package com.acme.accountingsrv.plan.mapper;

import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.commons.mapper.StringMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CompanyPlanMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(source = "plan", target = "plan")
    CompanyPlanDto toDto(CompanyPlan source, PlanDto plan);
}
