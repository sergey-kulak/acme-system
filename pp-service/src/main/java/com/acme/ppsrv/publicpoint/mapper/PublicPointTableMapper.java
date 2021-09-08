package com.acme.ppsrv.publicpoint.mapper;

import com.acme.commons.mapper.StringMapper;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.SavePpTableDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface PublicPointTableMapper {
    @Mapping(source = "ppId", target = "publicPointId")
    PublicPointTable fromDto(SavePpTableDto dto, UUID ppId);
}
