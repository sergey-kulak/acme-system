package com.acme.ppsrv.order.service;

import com.acme.commons.security.ClientAuthenticated;
import com.acme.commons.security.NotAccountantAuthenticated;
import com.acme.commons.security.NotAccountantUserAuthenticated;
import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.order.dto.CreateOrderDto;
import com.acme.ppsrv.order.dto.LiveOrderFilter;
import com.acme.ppsrv.order.dto.OrderDto;
import com.acme.ppsrv.order.dto.OrderFilter;
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
public interface OrderService {
    @ClientAuthenticated
    Mono<UUID> create(@Valid CreateOrderDto dto);

    @NotAccountantAuthenticated
    Mono<OrderDto> findById(UUID id);

    @NotAccountantUserAuthenticated
    Mono<Page<SummaryOrderDto>> find(OrderFilter filter, Pageable pageable);

    @NotAccountantUserAuthenticated
    Mono<List<OrderDto>> findLiveOrders(LiveOrderFilter filter);

    @NotAccountantUserAuthenticated
    Mono<Void> changeStatus(UUID orderId, OrderStatus newStatus);

    @NotAccountantUserAuthenticated
    Mono<Void> changeStatus(UUID itemId, OrderItemStatus newStatus);
}
