package com.acme.ppsrv.order.dto;

import com.acme.ppsrv.order.OrderItemStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class OrderItemDto {
    private UUID id;
    private OrderItemStatus status;
    private UUID dishId;
    private String dishName;
    private BigDecimal price;
    private int quantity;
    private Instant createdDate;
    private Instant doneDate;
    private String comment;
}
