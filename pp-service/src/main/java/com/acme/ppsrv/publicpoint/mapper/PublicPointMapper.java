package com.acme.ppsrv.publicpoint.mapper;

import com.acme.commons.mapper.StringMapper;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.dto.CreatePublicPointDto;
import com.acme.ppsrv.publicpoint.dto.FullDetailsPublicPointDto;
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface PublicPointMapper {
    @Mapping(target = "primaryLang", source = "primaryLang", qualifiedByName = {"String", "toLowercase"})
    PublicPoint fromDto(CreatePublicPointDto source);

    @Mapping(target = "primaryLang", source = "primaryLang", qualifiedByName = {"String", "toLowercase"})
    void update(@MappingTarget PublicPoint target, UpdatePublicPointDto source);

    @Mapping(source = "langs", target = "langs")
    FullDetailsPublicPointDto toDto(PublicPoint source, List<String> langs);
}
