package com.acme.ppsrv.order.event;

import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationEvent;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderStatusChangedEvent implements NotificationEvent {
    private UUID orderId;
    private UUID companyId;
    private UUID publicPointId;
    private UUID tableId;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;

    @Tolerate
    public OrderStatusChangedEvent() {
    }

    @Override
    public NotificationDto convert() {
        return NotificationDto.builder()
                .type(this.getClass().getSimpleName())
                .companyId(companyId)
                .publicPointId(publicPointId)
                .tableId(tableId)
                .data(Map.of(
                        "orderId", orderId,
                        "fromStatus", fromStatus,
                        "toStatus", toStatus
                ))
                .build();
    }
}
