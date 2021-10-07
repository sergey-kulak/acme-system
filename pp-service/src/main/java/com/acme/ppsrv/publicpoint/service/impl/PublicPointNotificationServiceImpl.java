package com.acme.ppsrv.publicpoint.service.impl;

import com.acme.commons.security.CompanyUserDetails;
import com.acme.commons.security.ParseTokenService;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.security.UserRole;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationRequest;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.ppsrv.publicpoint.service.PublicPointNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicPointNotificationServiceImpl implements PublicPointNotificationService {
    private static final List<String> CLIENT_EVENT_TYPES =
            List.of("OrderStatusChangedEvent", "OrderItemStatusChangedEvent");
    private static final String CALL_WAITER_MESSAGE = "Table '%s' is calling a waiter";
    private static final String CALL_WAITER_TYPE = "CallWaiterEvent";

    private UUID id = UUID.randomUUID();
    private final ParseTokenService parseTokenService;
    private final FluxMessageChannel notificationChannel;
    private final StreamBridge streamBridge;
    private final PublicPointTableRepository tableRepository;

    @Override
    public Flux<NotificationDto> getNotifications(NotificationRequest request, String token) {
        return parseToken(token)
                .filter(user -> hasAccess(user, request))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")))
                .flatMapMany(user -> filter(user, request));
    }

    private Flux<NotificationDto> filter(CompanyUserDetails user, NotificationRequest request) {
        log.info("user {} connected to {}", user.getUsername(), id);
        return Flux.concat(notificationChannel)
                .map(message -> (NotificationDto) message.getPayload())
                .filter(notification -> filter(user, request, notification));
    }

    private boolean filter(CompanyUserDetails user, NotificationRequest request,
                           NotificationDto notification) {
        log.info("receive message {} for user {} connected to {}", notification.getType(), user.getUsername(), id);
        boolean hasAccess = true;
        boolean isRequested = Objects.equals(request.getCompanyId(), notification.getCompanyId())
                && Objects.equals(request.getPublicPointId(), notification.getPublicPointId());

        if (isClient(user)) {
            hasAccess = Objects.equals(user.getId(), notification.getTableId());
        }

        return hasAccess && isRequested && filterByContent(user, notification);
    }

    private boolean hasAccess(CompanyUserDetails user, NotificationRequest request) {
        return SecurityUtils.hasAccess(user, request.getCompanyId(), false) &&
                SecurityUtils.hasPpAccess(user, request.getPublicPointId());
    }

    private boolean filterByContent(CompanyUserDetails user, NotificationDto notification) {
        String type = notification.getType();
        if (isClient(user)) {
            return CLIENT_EVENT_TYPES.contains(type);
        } else {
            return !CALL_WAITER_TYPE.equals(type)
                    || user.hasAnyRole(UserRole.PP_MANAGER, UserRole.WAITER);
        }
    }

    private boolean isClient(CompanyUserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("CLIENT"));
    }

    private Mono<CompanyUserDetails> parseToken(String token) {
        return parseTokenService.parseAccessToken(token)
                .map(authentication -> (CompanyUserDetails) authentication.getPrincipal());
    }

    @Override
    public Mono<Void> callWaiter() {
        return SecurityUtils.getCurrentUser()
                .flatMap(this::callWaiterByTable);
    }

    private Mono<Void> callWaiterByTable(CompanyUserDetails user) {
        return tableRepository.findById(user.getId())
                .map(table -> buildNotification(user, table))
                .flatMap(this::broadcast);
    }

    private NotificationDto buildNotification(CompanyUserDetails user, PublicPointTable table) {
        return NotificationDto.builder()
                .companyId(user.getCompanyId())
                .publicPointId(user.getPublicPointId())
                .tableId(table.getId())
                .type(CALL_WAITER_TYPE)
                .data(Map.of(
                        "tableName", table.getName(),
                        "message", String.format(CALL_WAITER_MESSAGE, table.getName())
                ))
                .build();
    }

    @Override
    public Mono<Void> broadcast(NotificationDto notification) {
        if (streamBridge.send("ppNotification-out-0", notification)) {
            return Mono.empty();
        } else {
            return Mono.error(new RuntimeException("Error during notification broadcast"));
        }
    }
}
