package com.acme.ppsrv.publicpoint.dto;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UpdatePublicPointDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    @NotBlank
    private String primaryLang;
    private Set<String> langs;

    @Tolerate
    public UpdatePublicPointDto() {

    }
}
