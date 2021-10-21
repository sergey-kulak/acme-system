package com.acme.ppsrv.order.service;

import com.acme.commons.exception.IllegalStatusChange;
import com.acme.commons.security.CompanyUserDetails;
import com.acme.commons.security.SecurityUtils;
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
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import com.acme.ppsrv.order.repository.OrderItemRepository;
import com.acme.ppsrv.order.repository.OrderRepository;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.test.ServiceIntegrationTest;
import com.acme.ppsrv.test.TestEntityHelper;
import com.acme.testcommons.TxStepVerifier;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockClient;
import com.acme.testcommons.security.WithMockCook;
import com.acme.testcommons.security.WithMockPpManager;
import com.acme.testcommons.security.WithMockWaiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderServiceIntegrationTest extends ServiceIntegrationTest{
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository itemRepository;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    void createDenied() {
        orderService.create(new CreateOrderDto())
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockClient
    void createValidation() {
        orderService.create(new CreateOrderDto())
                .as(TxStepVerifier::withRollback)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @WithMockClient
    void create() {
        UUID companyId = UUID.randomUUID();
        CreateOrderDto dto = CreateOrderDto.builder()
                .items(List.of(CreateOrderItemDto.builder()
                        .dishId(UUID.randomUUID())
                        .dishName("Tasty dish")
                        .quantity(2)
                        .price(new BigDecimal("9.99"))
                        .build()))
                .build();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table ->
                        TestSecurityUtils.linkWithCurrentUser(companyId, table.getPublicPointId(), table.getId()))
                .then(SecurityUtils.getCurrentUser())
                .zipWhen(user ->
                        orderService.create(dto)
                                .flatMap(id -> Mono.zip(
                                        orderRepository.findById(id),
                                        itemRepository.findByOrderIds(List.of(id)).collectList()

                                ))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    CompanyUserDetails user = data.getT1();
                    Order order = data.getT2().getT1();
                    List<OrderItem> items = data.getT2().getT2();

                    assertThat(order, allOf(
                            hasProperty("companyId", is(user.getCompanyId())),
                            hasProperty("publicPointId", is(user.getPublicPointId())),
                            hasProperty("tableId", is(user.getId())),
                            hasProperty("number", notNullValue()),
                            hasProperty("createdDate", notNullValue()),
                            hasProperty("status", is(OrderStatus.CREATED))
                    ));

                    assertEquals(1, items.size());
                    CreateOrderItemDto itemDto = dto.getItems().get(0);
                    assertThat(items.get(0), allOf(
                            hasProperty("orderId", is(order.getId())),
                            hasProperty("dishId", is(itemDto.getDishId())),
                            hasProperty("dishName", is(itemDto.getDishName())),
                            hasProperty("quantity", is(itemDto.getQuantity())),
                            hasProperty("price", is(itemDto.getPrice())),
                            hasProperty("status", is(OrderItemStatus.CREATED)),
                            hasProperty("createdDate", notNullValue())
                    ));
                })
                .verifyComplete();
    }

    @Test
    @WithMockWaiter
    void findById() {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> testEntityHelper.createOrder(companyId, table.getPublicPointId(), table.getId()))
                .zipWhen(order -> Mono.zip(
                        itemRepository.findByOrderIds(List.of(order.getId())).collectList(),
                        orderService.findById(order.getId())
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Order order = data.getT1();
                    List<OrderItem> items = data.getT2().getT1();
                    OrderDto dto = data.getT2().getT2();

                    assertThat(dto, allOf(
                            hasProperty("id", is(order.getId())),
                            hasProperty("companyId", is(order.getCompanyId())),
                            hasProperty("publicPointId", is(order.getPublicPointId())),
                            hasProperty("tableId", is(order.getTableId())),
                            hasProperty("number", is(order.getNumber())),
                            hasProperty("createdDate", is(order.getCreatedDate())),
                            hasProperty("status", is(order.getStatus()))
                    ));

                    assertEquals(items.size(), dto.getItems().size());
                    assertThat(dto.getItems(), containsInAnyOrder(StreamUtils.mapToList(items, item -> allOf(
                            hasProperty("id", is(item.getId())),
                            hasProperty("dishId", is(item.getDishId())),
                            hasProperty("dishName", is(item.getDishName())),
                            hasProperty("price", is(item.getPrice())),
                            hasProperty("quantity", is(item.getQuantity())),
                            hasProperty("comment", is(item.getComment())),
                            hasProperty("createdDate", is(item.getCreatedDate())),
                            hasProperty("status", is(item.getStatus()))
                    ))));
                })
                .verifyComplete();
    }

    @Test
    @WithMockWaiter
    void find() {
        UUID companyId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created_date"));

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> testEntityHelper.createOrder(companyId, table.getPublicPointId(), table.getId()))
                .zipWhen(order -> itemRepository.findByOrderIds(List.of(order.getId())).collectList())
                .zipWhen(data -> {
                    Order order = data.getT1();
                    OrderItem item = data.getT2().get(0);
                    OrderFilter filter = OrderFilter.builder()
                            .companyId(companyId)
                            .publicPointId(order.getPublicPointId())
                            .status(order.getStatus())
                            .dishId(item.getDishId())
                            .fromTotalPrice(BigDecimal.ONE)
                            .toTotalPrice(new BigDecimal("1000"))
                            .number(order.getNumber().toUpperCase())
                            .fromCreatedDate(LocalDate.ofInstant(order.getCreatedDate(), ZoneId.systemDefault()))
                            .toCreatedDate(LocalDate.ofInstant(order.getCreatedDate(), ZoneId.systemDefault()))
                            .build();
                    return orderService.find(filter, pageable);
                })
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Order order = data.getT1().getT1();
                    OrderItem item = data.getT1().getT2().get(0);
                    BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    Page<SummaryOrderDto> page = data.getT2();

                    assertEquals(1, page.getTotalElements());
                    assertThat(page.getContent().get(0), allOf(
                            hasProperty("id", is(order.getId())),
                            hasProperty("number", is(order.getNumber())),
                            hasProperty("status", is(order.getStatus())),
                            hasProperty("createdDate", is(order.getCreatedDate())),
                            hasProperty("dishCount", is(1)),
                            hasProperty("totalPrice", is(totalPrice))
                    ));
                })
                .verifyComplete();
    }

    private Mono<Order> createOrder(UUID companyId, PublicPointTable table, OrderStatus status) {
        return testEntityHelper.createOrder(companyId, table.getPublicPointId(), table.getId())
                .flatMap(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                });
    }

    private Mono<OrderItem> setItemStatus(Order order, OrderItemStatus status) {
        return itemRepository.findByOrderIds(List.of(order.getId()))
                .collectList()
                .map(items -> items.get(0))
                .flatMap(item -> {
                    item.setStatus(status);
                    return itemRepository.save(item);
                });
    }

    @Test
    @WithMockWaiter
    void findLive() {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .zipWhen(table -> Mono.zip(
                        createOrder(companyId, table, OrderStatus.CONFIRMED),
                        createOrder(companyId, table, OrderStatus.DECLINED)
                ))
                .zipWhen(data -> orderService.findLiveOrders(LiveOrderFilter.builder()
                        .companyId(companyId)
                        .publicPointId(data.getT1().getPublicPointId())
                        .build()))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Order confirmedOrder = data.getT1().getT2().getT1();
                    List<OrderDto> liveOrders = data.getT2();
                    assertEquals(1, liveOrders.size());
                    assertThat(liveOrders.get(0), allOf(
                            hasProperty("id", is(confirmedOrder.getId()))
                    ));
                })
                .verifyComplete();
    }

    private void allowedStatusChange(OrderStatus fromStatus, OrderStatus toStatus) {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> createOrder(companyId, table, fromStatus))
                .flatMap(order -> orderService.changeStatus(order.getId(), toStatus)
                        .then(orderRepository.findById(order.getId())))
                .as(TxStepVerifier::withRollback)
                .assertNext(order -> assertEquals(toStatus, order.getStatus()))
                .verifyComplete();
    }

    private void disallowedStatusChange(OrderStatus fromStatus, OrderStatus toStatus) {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> createOrder(companyId, table, fromStatus))
                .flatMap(order -> orderService.changeStatus(order.getId(), toStatus))
                .as(TxStepVerifier::withRollback)
                .expectError(IllegalStatusChange.class)
                .verify();
    }

    @Test
    @WithMockWaiter
    void changeStatusDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table -> createOrder(companyId, table, OrderStatus.CREATED))
                .flatMap(order -> orderService.changeStatus(order.getId(), OrderStatus.CONFIRMED))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockPpManager
    void createdStatusToConfirmed() {
        allowedStatusChange(OrderStatus.CREATED, OrderStatus.CONFIRMED);
    }

    @Test
    @WithMockPpManager
    void createdStatusToDeclined() {
        allowedStatusChange(OrderStatus.CREATED, OrderStatus.DECLINED);
    }

    @Test
    @WithMockPpManager
    void confirmedStatusToInProgressDenied() {
        disallowedStatusChange(OrderStatus.CONFIRMED, OrderStatus.IN_PROGRESS);
    }

    @Test
    @WithMockPpManager
    void inProgressStatusToReadyDenied() {
        disallowedStatusChange(OrderStatus.IN_PROGRESS, OrderStatus.READY);
    }

    @Test
    @WithMockWaiter
    void readyStatusToDelivered() {
        allowedStatusChange(OrderStatus.READY, OrderStatus.DELIVERED);
    }

    @Test
    @WithMockWaiter
    void deliveredStatusToPaid() {
        allowedStatusChange(OrderStatus.DELIVERED, OrderStatus.PAID);
    }

    private void allowedStatusChange(OrderStatus orderFromStatus,
                                     OrderItemStatus itemFromStatus,
                                     OrderStatus orderToStatus,
                                     OrderItemStatus itemToStatus) {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> createOrder(companyId, table, orderFromStatus))
                .zipWhen(order -> setItemStatus(order, itemFromStatus))
                .flatMap(data -> orderService.changeStatus(data.getT2().getId(), itemToStatus)
                        .then(Mono.zip(
                                orderRepository.findById(data.getT1().getId()),
                                itemRepository.findById(data.getT2().getId())
                        )))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    assertEquals(orderToStatus, data.getT1().getStatus());
                    assertEquals(itemToStatus, data.getT2().getStatus());
                })
                .verifyComplete();
    }

    private void disallowedStatusChange(OrderStatus orderFromStatus,
                                        OrderItemStatus itemFromStatus,
                                        OrderItemStatus itemToStatus) {
        UUID companyId = UUID.randomUUID();

        testEntityHelper.createPublicPoint(companyId)
                .flatMap(pp -> TestSecurityUtils.linkWithCurrentUser(companyId, pp.getId())
                        .then(testEntityHelper.createTable(pp.getId())))
                .flatMap(table -> createOrder(companyId, table, orderFromStatus))
                .zipWhen(order -> setItemStatus(order, itemFromStatus))
                .flatMap(data -> orderService.changeStatus(data.getT2().getId(), itemToStatus))
                .as(TxStepVerifier::withRollback)
                .expectError(IllegalStatusChange.class)
                .verify();
    }

    @Test
    @WithMockWaiter
    void itemChangeStatusDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table -> createOrder(companyId, table, OrderStatus.CREATED))
                .flatMap(order -> setItemStatus(order, OrderItemStatus.CREATED))
                .flatMap(item -> orderService.changeStatus(item.getId(), OrderItemStatus.IN_PROGRESS))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCook
    void createdStatusToInProgress() {
        allowedStatusChange(OrderStatus.CONFIRMED, OrderItemStatus.CREATED,
                OrderStatus.IN_PROGRESS, OrderItemStatus.IN_PROGRESS);
    }

    @Test
    @WithMockCook
    void createdStatusToInProgressWhenOrderCreatedDenied() {
        disallowedStatusChange(OrderStatus.CREATED, OrderItemStatus.CREATED, OrderItemStatus.IN_PROGRESS);
    }

    @Test
    @WithMockCook
    void createdItemStatusToDeclined() {
        allowedStatusChange(OrderStatus.CONFIRMED, OrderItemStatus.CREATED,
                OrderStatus.DECLINED, OrderItemStatus.DECLINED);
    }

    @Test
    @WithMockCook
    void inProgressStatusToDone() {
        allowedStatusChange(OrderStatus.CONFIRMED, OrderItemStatus.IN_PROGRESS,
                OrderStatus.READY, OrderItemStatus.DONE);
    }
}