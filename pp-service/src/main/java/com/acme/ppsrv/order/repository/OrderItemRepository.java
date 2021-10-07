package com.acme.ppsrv.order.repository;

import com.acme.ppsrv.order.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends ReactiveSortingRepository<OrderItem, UUID> {

    @Query("select oi.* from order_item oi where oi.order_id in (:ids)")
    Flux<OrderItem> findByOrderIds(@Param("ids") Collection<UUID> orderId);

    @Query("select count(distinct status) from order_item where order_id = $1")
    Mono<Integer> countDistinctStatuses(UUID orderId);

    @Query("select count(1) from order_item where order_id = $1 " +
            "and status not in ('DONE', 'DECLINED')")
    Mono<Integer> countNotCompletedItems(UUID orderId);
}
