package com.acme.accountingsrv.plan.mapper;

import com.acme.accountingsrv.plan.PublicPointPlan;
import com.acme.accountingsrv.plan.dto.PublicPointPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyPlanMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(source = "plan", target = "plan")
    PublicPointPlanDto toDto(PublicPointPlan source, PlanDto plan);
}
