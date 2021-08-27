package com.acme.rfdatasrv.lang.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Language")
public class LangDto {
    private String code;
    private String name;
    private String nativeName;
}
