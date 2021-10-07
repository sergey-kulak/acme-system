package com.acme.ppsrv.order.dto;

import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Order status request")
public class OrderStatusDto {
    @NotNull
    private OrderStatus status;
}
