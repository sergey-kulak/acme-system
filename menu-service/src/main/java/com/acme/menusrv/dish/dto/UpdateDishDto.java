package com.acme.menusrv.dish.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
public class UpdateDishDto {
    @NotBlank
    private String name;
    private String description;
    private String composition;
    private List<String> tags;
    @NotBlank
    private String primaryImage;
    private List<String> images;


}
