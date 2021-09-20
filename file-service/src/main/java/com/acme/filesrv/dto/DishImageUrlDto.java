package com.acme.filesrv.dto;

import com.acme.filesrv.Action;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DishImageUrlDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    @NotNull
    private Action action;
    private List<String> imageKeys;
}
