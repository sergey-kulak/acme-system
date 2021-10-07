package com.acme.ppsrv.order.dto;

import com.acme.ppsrv.order.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private UUID companyId;
    private UUID publicPointId;
    private UUID tableId;
    private String number;
    private OrderStatus status;
    private Instant createdDate;
    private Instant paidDate;
    private List<OrderItemDto> items;

    public BigDecimal getTotalPrice() {
        return getItems().stream()
                .reduce(BigDecimal.ZERO,
                        (acc, item) -> acc.add(item.getPrice()
                                .multiply(new BigDecimal(item.getQuantity()))),
                        BigDecimal::add);
    }
}
