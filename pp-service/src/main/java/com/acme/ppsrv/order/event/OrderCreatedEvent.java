package com.acme.ppsrv.order.event;

import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationEvent;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderCreatedEvent implements NotificationEvent {
    private UUID orderId;
    private UUID companyId;
    private UUID publicPointId;
    private UUID tableId;

    @Tolerate
    public OrderCreatedEvent() {
    }

    @Override
    public NotificationDto convert() {
        return NotificationDto.builder()
                .type(this.getClass().getSimpleName())
                .companyId(companyId)
                .publicPointId(publicPointId)
                .tableId(tableId)
                .data(Map.of("orderId", orderId))
                .build();
    }
}
