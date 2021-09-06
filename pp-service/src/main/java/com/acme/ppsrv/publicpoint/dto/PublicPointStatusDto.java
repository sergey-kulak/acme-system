package com.acme.ppsrv.publicpoint.dto;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Public point status request")
public class PublicPointStatusDto {
    @NotNull
    private PublicPointStatus status;
}
