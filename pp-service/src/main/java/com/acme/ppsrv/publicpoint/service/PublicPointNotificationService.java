package com.acme.ppsrv.publicpoint.service;

import com.acme.commons.security.ClientAuthenticated;
import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationRequest;
import org.springframework.messaging.handler.annotation.Header;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PublicPointNotificationService {

    Flux<NotificationDto> getNotifications(NotificationRequest request, @Header String token);

    @ClientAuthenticated
    Mono<Void> callWaiter();

    Mono<Void> broadcast(NotificationDto notification);
}
