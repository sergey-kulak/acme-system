package com.acme.ppsrv.order.repository;

import com.acme.ppsrv.order.Order;
import com.acme.ppsrv.order.dto.LiveOrderFilter;
import com.acme.ppsrv.order.dto.OrderFilter;
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepositoryCustom {
    Mono<Page<SummaryOrderDto>> find(OrderFilter filter, Pageable pageable);

    Flux<Order> findLiveOrders(LiveOrderFilter filter);
}
