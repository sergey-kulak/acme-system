package com.acme.ppsrv.test;

import com.acme.ppsrv.order.Order;
import com.acme.ppsrv.order.OrderItem;
import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.order.repository.OrderItemRepository;
import com.acme.ppsrv.order.repository.OrderRepository;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.testcommons.RandomTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private PublicPointRepository ppRepository;
    @Autowired
    private PublicPointTableRepository ppTableRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Mono<PublicPoint> createPublicPoint(UUID companyId) {
        return createPublicPoint(companyId, PublicPointStatus.ACTIVE);
    }

    public Mono<PublicPoint> createPublicPoint(UUID companyId, PublicPointStatus status) {
        PublicPoint publicPoint = new PublicPoint();
        publicPoint.setCompanyId(companyId);
        publicPoint.setStatus(status);
        publicPoint.setName(RandomTestUtils.randomString("Plan"));
        publicPoint.setDescription(RandomTestUtils.randomString("Descr"));
        publicPoint.setCity(RandomTestUtils.randomString("City"));
        publicPoint.setAddress(RandomTestUtils.randomString("Address"));
        publicPoint.setPrimaryLang("ru");

        return ppRepository.save(publicPoint)
                .flatMap(pp -> ppRepository.addLang(pp.getId(), "en")
                        .thenReturn(pp)
                );
    }

    public Mono<PublicPointTable> createTable(UUID publicPointId) {
        PublicPointTable table = new PublicPointTable();
        table.setName(RandomTestUtils.randomString("table"));
        table.setDescription(RandomTestUtils.randomString("descr"));
        table.setPublicPointId(publicPointId);
        table.setSeatCount(RandomUtils.nextInt(4, 20));
        table.setCode(RandomStringUtils.randomAlphanumeric(50));

        return ppTableRepository.save(table);
    }

    public Mono<Order> createOrder(UUID companyId, UUID publicPointId, UUID tableId) {
        Order order = new Order();
        order.setCompanyId(companyId);
        order.setPublicPointId(publicPointId);
        order.setTableId(tableId);
        order.setCreatedDate(Instant.now());
        order.setStatus(OrderStatus.CREATED);
        order.setNumber(RandomStringUtils.randomAlphanumeric(10));

        return orderRepository.save(order)
                .flatMap(savedOrder -> createOrderItem(savedOrder.getId())
                        .thenReturn(savedOrder));
    }

    private Mono<OrderItem> createOrderItem(UUID orderId) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setStatus(OrderItemStatus.CREATED);
        item.setDishId(UUID.randomUUID());
        item.setDishName(RandomTestUtils.randomString("dish"));
        item.setCreatedDate(Instant.now());
        item.setQuantity(2);
        item.setPrice(new BigDecimal("5.5"));
        item.setComment(RandomTestUtils.randomString("comment"));

        return orderItemRepository.save(item);
    }
}
