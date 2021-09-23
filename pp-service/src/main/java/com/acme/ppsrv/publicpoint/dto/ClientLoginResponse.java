package com.acme.ppsrv.publicpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class ClientLoginResponse {
    private String publicPointName;
    private String accessToken;

    @Tolerate
    public ClientLoginResponse() {
    }
}
