package com.acme.ppsrv.order.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class CreateOrderDto {
    @Valid
    @NotEmpty
    private List<CreateOrderItemDto> items;

    @Tolerate
    public CreateOrderDto() {
    }
}
