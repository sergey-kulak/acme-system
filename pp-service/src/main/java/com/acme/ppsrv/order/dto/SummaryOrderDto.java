package com.acme.ppsrv.order.dto;

import com.acme.ppsrv.order.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class SummaryOrderDto {
    private UUID id;
    private String number;
    private OrderStatus status;
    private Instant createdDate;
    private Instant paidDate;
    private int dishCount;
    private BigDecimal totalPrice;
}
