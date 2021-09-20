package com.acme.menusrv.dish.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateDishDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    @NotBlank
    private String name;
    private String description;
    private String composition;
    private List<String> tags;
    @NotBlank
    private String primaryImage;
    private List<String> images;

    @Tolerate
    public CreateDishDto() {
    }
}
