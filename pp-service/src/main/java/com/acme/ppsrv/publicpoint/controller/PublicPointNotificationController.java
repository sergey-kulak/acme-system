package com.acme.ppsrv.publicpoint.controller;

import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationRequest;
import com.acme.ppsrv.publicpoint.service.PublicPointNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PublicPointNotificationController {
    private final PublicPointNotificationService notificationService;


    @MessageMapping("get.notifications")
    public Flux<NotificationDto> getNotifications(NotificationRequest request, @Header String token) {
        return notificationService.getNotifications(request, token);
    }
}
