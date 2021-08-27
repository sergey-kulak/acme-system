package com.acme.rfdatasrv.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Country")
public class CountryDto {
    private String code;
    private String name;
}
