package com.acme.ppsrv.publicpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class SavePpTableDto {
    private UUID id;
    @NotBlank
    private String name;
    @Min(1)
    private int seatCount;
    private String description;

    @Tolerate
    public SavePpTableDto() {
    }
}
