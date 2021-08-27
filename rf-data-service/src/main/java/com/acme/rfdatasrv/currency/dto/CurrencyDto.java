package com.acme.rfdatasrv.currency.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Currency")
public class CurrencyDto {
    private String code;
    private String name;
    private String symbol;
}
