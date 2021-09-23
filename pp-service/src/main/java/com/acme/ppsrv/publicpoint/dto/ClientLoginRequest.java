package com.acme.ppsrv.publicpoint.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientLoginRequest {
    @NotNull
    private String code;
}
