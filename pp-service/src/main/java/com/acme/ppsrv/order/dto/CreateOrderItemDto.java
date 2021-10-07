package com.acme.ppsrv.order.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateOrderItemDto {
    @NotNull
    private UUID dishId;
    @NotBlank
    private String dishName;
    @NotNull
    private BigDecimal price;
    @NotNull
    @Min(0)
    private int quantity;
    private String comment;

    @Tolerate
    public CreateOrderItemDto() {
    }
}
