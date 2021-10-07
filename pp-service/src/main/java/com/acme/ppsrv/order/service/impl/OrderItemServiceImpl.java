package com.acme.ppsrv.order.service.impl;

import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.commons.repository.RepoUtils;
import com.acme.commons.security.CompanyUserDetails;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.utils.CollectionUtils;
import com.acme.commons.utils.StreamUtils;
import com.acme.ppsrv.order.Order;
import com.acme.ppsrv.order.OrderItem;
import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.order.dto.CreateOrderDto;
import com.acme.ppsrv.order.dto.CreateOrderItemDto;
import com.acme.ppsrv.order.dto.LiveOrderFilter;
import com.acme.ppsrv.order.dto.OrderDto;
import com.acme.ppsrv.order.dto.OrderFilter;
import com.acme.ppsrv.order.dto.OrderItemDto;
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import com.acme.ppsrv.order.event.OrderCreatedEvent;
import com.acme.ppsrv.order.event.OrderItemStatusChangedEvent;
import com.acme.ppsrv.order.event.OrderStatusChangedEvent;
import com.acme.ppsrv.order.mapper.OrderMapper;
import com.acme.ppsrv.order.repository.OrderItemRepository;
import com.acme.ppsrv.order.repository.OrderRepository;
import com.acme.ppsrv.order.service.OrderService;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.event.PublicPointStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderService {
    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_ORDER_NEXT_STATUSES = Map.of(
            OrderStatus.CREATED, List.of(OrderStatus.CONFIRMED, OrderStatus.DECLINED),
            OrderStatus.READY, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.PAID)
    );

    private static final Map<OrderItemStatus, List<OrderItemStatus>> ALLOWED_ITEM_NEXT_STATUSES = Map.of(
            OrderItemStatus.CREATED, List.of(OrderItemStatus.IN_PROGRESS, OrderItemStatus.DECLINED),
            OrderItemStatus.IN_PROGRESS, List.of(OrderItemStatus.DONE, OrderItemStatus.DECLINED)
    );

    private static final List<OrderStatus> ALLOWED_STATUSES_FOR_ITEM_UPDATES =
            List.of(OrderStatus.CONFIRMED, OrderStatus.IN_PROGRESS);

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final OrderMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ReactiveTransactionManager txManager;

    @Override
    public Mono<UUID> create(CreateOrderDto dto) {
        return SecurityUtils.getCurrentUser()
                .flatMap(client -> internalCreate(client, dto))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(order -> notify(OrderCreatedEvent.builder()
                        .companyId(order.getCompanyId())
                        .publicPointId(order.getPublicPointId())
                        .tableId(order.getTableId())
                        .orderId(order.getId())
                        .build()))
                .map(Order::getId);
    }

    private Mono<Order> internalCreate(CompanyUserDetails client, CreateOrderDto dto) {
        Order order = new Order();
        order.setCompanyId(client.getCompanyId());
        order.setPublicPointId(client.getPublicPointId());
        order.setStatus(OrderStatus.CREATED);
        order.setTableId(client.getId());
        order.setCreatedDate(Instant.now());
        order.setNumber(generateNumber());

        return orderRepository.save(order)
                .flatMap(savedOrder -> addItems(dto, savedOrder));
    }

    private Mono<Order> addItems(CreateOrderDto dto, Order order) {
        List<Mono<OrderItem>> itemMonos = dto.getItems()
                .stream()
                .map(item -> map(item, order))
                .map(itemRepository::save)
                .collect(Collectors.toList());

        return Mono.when(itemMonos)
                .thenReturn(order);
    }

    private OrderItem map(CreateOrderItemDto dto, Order order) {
        OrderItem item = mapper.fromDto(dto);
        item.setOrderId(order.getId());
        item.setStatus(OrderItemStatus.CREATED);
        item.setCreatedDate(Instant.now());
        return item;
    }

    private String generateNumber() {
        return String.format("%s-%s", RandomStringUtils.randomNumeric(2),
                RandomStringUtils.randomAlphabetic(6).toLowerCase());
    }

    private Mono<Order> checkAccess(Order order) {
        return SecurityUtils.isPpAccessible(order.getCompanyId(), order.getPublicPointId())
                .thenReturn(order);
    }

    private Mono<Tuple2<OrderItem, Order>> checkAccess(Tuple2<OrderItem, Order> data) {
        Order order = data.getT2();
        return SecurityUtils.isPpAccessible(order.getCompanyId(), order.getPublicPointId())
                .thenReturn(data);
    }

    @Override
    public Mono<OrderDto> findById(UUID id) {
        return orderRepository.findById(id)
                .flatMap(this::checkAccess)
                .zipWhen(order -> itemRepository.findByOrderIds(List.of(order.getId())).collectList())
                .map(data -> mapper.toDto(data.getT1(), data.getT2()))
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Mono<Page<SummaryOrderDto>> find(OrderFilter filter, Pageable pageable) {
        return SecurityUtils.isPpAccessible(filter.getCompanyId(), filter.getPublicPointId())
                .then(orderRepository.find(filter, pageable));
    }

    @Override
    public Mono<List<OrderDto>> findLiveOrders(LiveOrderFilter filter) {
        return SecurityUtils.isPpAccessible(filter.getCompanyId(), filter.getPublicPointId())
                .then(orderRepository.findLiveOrders(filter).collectList())
                .zipWhen(this::findItems)
                .map(data -> map(data.getT1(), data.getT2()));
    }

    private Mono<Map<UUID, List<OrderItem>>> findItems(List<Order> orders) {
        List<UUID> ids = StreamUtils.mapToList(orders, Order::getId);
        return CollectionUtils.isEmpty(ids) ? Mono.just(Map.of()) : itemRepository.findByOrderIds(ids)
                .collectList()
                .map(items -> StreamUtils.groupToLists(items, OrderItem::getOrderId));
    }

    private List<OrderDto> map(List<Order> orders, Map<UUID, List<OrderItem>> items) {
        return StreamUtils.mapToList(orders,
                order -> mapper.toDto(order, items.getOrDefault(order.getId(), List.of())));
    }

    public Mono<Void> changeStatus(UUID orderId, OrderStatus newStatus) {
        return orderRepository.findById(orderId)
                .flatMap(this::checkAccess)
                .flatMap(order -> isValidChange(order, newStatus))
                .flatMap(order -> saveStatus(order, newStatus))
                .switchIfEmpty(EntityNotFoundException.of(orderId))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(this::notify)
                .then();
    }

    private Mono<OrderStatusChangedEvent> saveStatus(Order order, OrderStatus newStatus) {
        OrderStatus fromStatus = order.getStatus();
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.PAID) {
            order.setPaidDate(Instant.now());
        }

        return orderRepository.save(order)
                .thenReturn(OrderStatusChangedEvent.builder()
                        .orderId(order.getId())
                        .publicPointId(order.getPublicPointId())
                        .companyId(order.getCompanyId())
                        .tableId(order.getTableId())
                        .fromStatus(fromStatus)
                        .toStatus(newStatus)
                        .build());
    }

    private Mono<Order> isValidChange(Order order, OrderStatus newStatus) {
        return RepoUtils.isValidChange(order, Order::getStatus, ALLOWED_ORDER_NEXT_STATUSES, newStatus);
    }

    private Mono<Tuple2<OrderItem, Order>> isValidChange(Tuple2<OrderItem, Order> data, OrderItemStatus newStatus) {
        OrderItem item = data.getT1();
        Order order = data.getT2();
        return RepoUtils.isValidChange(item, OrderItem::getStatus, ALLOWED_ITEM_NEXT_STATUSES, newStatus)
                .filter(rItem -> ALLOWED_STATUSES_FOR_ITEM_UPDATES.contains(order.getStatus()))
                .switchIfEmpty(IllegalStatusChange.of())
                .thenReturn(data);
    }

    private void notify(Object event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public Mono<Void> changeStatus(UUID itemId, OrderItemStatus newStatus) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(EntityNotFoundException.of(itemId))
                .zipWhen(item -> orderRepository.findById(item.getOrderId()))
                .flatMap(this::checkAccess)
                .flatMap(data -> isValidChange(data, newStatus))
                .flatMap(data -> saveStatus(data, newStatus))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(events -> events.forEach(this::notify))
                .then();
    }

    private Mono<List<Object>> saveStatus(Tuple2<OrderItem, Order> data,
                                          OrderItemStatus newStatus) {
        OrderItem item = data.getT1();
        Order order = data.getT2();
        OrderItemStatus fromStatus = item.getStatus();
        item.setStatus(newStatus);
        if (newStatus == OrderItemStatus.DONE) {
            item.setDoneDate(Instant.now());
        }

        Mono<Object> itemUpdate = itemRepository.save(item)
                .thenReturn(OrderItemStatusChangedEvent.builder()
                        .itemId(item.getId())
                        .orderId(order.getId())
                        .publicPointId(order.getPublicPointId())
                        .companyId(order.getCompanyId())
                        .tableId(order.getTableId())
                        .fromStatus(fromStatus)
                        .toStatus(newStatus)
                        .build());

        Mono<?> orderUpdate = itemUpdate.then(updateOrderStatus(item, order));

        return Flux.concat(itemUpdate, orderUpdate)
                .collectList();
    }

    private Mono<?> updateOrderStatus(OrderItem item, Order order) {
        if (order.getStatus() == OrderStatus.CONFIRMED
                && item.getStatus() == OrderItemStatus.IN_PROGRESS) {
            return saveStatus(order, OrderStatus.IN_PROGRESS);
        }
        if (item.getStatus() == OrderItemStatus.DECLINED) {
            return itemRepository.countDistinctStatuses(order.getId())
                    .filter(count -> count == 1)
                    .flatMap(count -> saveStatus(order, OrderStatus.DECLINED));
        }
        if (item.getStatus() == OrderItemStatus.DONE) {
            return itemRepository.countNotCompletedItems(order.getId())
                    .filter(count -> count == 0)
                    .flatMap(count -> saveStatus(order, OrderStatus.READY));
        }
        return Mono.empty();
    }
}
