package com.acme.ppsrv.order.event;

import com.acme.ppsrv.publicpoint.dto.NotificationEvent;
import com.acme.ppsrv.publicpoint.service.PublicPointNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final PublicPointNotificationService notificationService;

    @EventListener
    public void process(OrderStatusChangedEvent event) {
        send(event);
    }

    private void send(NotificationEvent event) {
        notificationService.broadcast(event.convert());
    }

    @EventListener
    public void process(OrderItemStatusChangedEvent event) {
        send(event);
    }

    @EventListener
    public void process(OrderCreatedEvent event) {
        send(event);
    }
}
