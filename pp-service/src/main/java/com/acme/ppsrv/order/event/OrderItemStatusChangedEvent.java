package com.acme.ppsrv.order.event;

import com.acme.ppsrv.order.OrderItemStatus;
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
public class OrderItemStatusChangedEvent implements NotificationEvent {
    private UUID itemId;
    private UUID orderId;
    private UUID companyId;
    private UUID publicPointId;
    private UUID tableId;
    private OrderItemStatus fromStatus;
    private OrderItemStatus toStatus;

    @Tolerate
    public OrderItemStatusChangedEvent() {
    }

    @Override
    public NotificationDto convert() {
        return NotificationDto.builder()
                .type(this.getClass().getSimpleName())
                .companyId(companyId)
                .publicPointId(publicPointId)
                .tableId(tableId)
                .data(Map.of(
                        "itemId", itemId,
                        "orderId", orderId,
                        "fromStatus", fromStatus,
                        "toStatus", toStatus
                ))
                .build();
    }
}
