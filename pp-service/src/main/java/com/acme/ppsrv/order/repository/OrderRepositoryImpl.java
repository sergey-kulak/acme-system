package com.acme.ppsrv.order.repository;

import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.CollectionUtils;
import com.acme.ppsrv.jooq.tables.OrderItem;
import com.acme.ppsrv.order.Order;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.order.dto.LiveOrderFilter;
import com.acme.ppsrv.order.dto.OrderFilter;
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

public class OrderRepositoryImpl extends AbstractCustomJooqRepository implements OrderRepositoryCustom {
    private static final com.acme.ppsrv.jooq.tables.Order ORDER =
            com.acme.ppsrv.jooq.tables.Order.ORDER.as("ord");
    private static final com.acme.ppsrv.jooq.tables.OrderItem ORDER_ITEM =
            com.acme.ppsrv.jooq.tables.OrderItem.ORDER_ITEM.as("itm");

    @Override
    public Mono<Page<SummaryOrderDto>> find(OrderFilter filter, Pageable pageable) {
        DSLContext dslContext = getDslContext();

        Table<?> table = filter.getDishId() != null ? ORDER.innerJoin(ORDER_ITEM)
                .on(ORDER.ID.eq(ORDER_ITEM.ORDER_ID)) : ORDER;
        Table<?> countTable = table;

        Table<Record3<UUID, BigDecimal, Integer>> ORDER_PRICE = dslContext
                .select(ORDER_ITEM.ORDER_ID,
                        sum(ORDER_ITEM.PRICE.mul(ORDER_ITEM.QUANTITY)).as("total_price"),
                        count(ORDER_ITEM.ID).as("dish_count"))
                .from(ORDER_ITEM)
                .groupBy(ORDER_ITEM.ORDER_ID)
                .asTable("op");

        Table<?> finalTable = table.innerJoin(ORDER_PRICE)
                .on(ORDER.ID.eq(ORDER_PRICE.field(0, UUID.class)));
        if (filter.getFromTotalPrice() != null || filter.getToTotalPrice() != null) {
            countTable = finalTable;
        }

        Field<BigDecimal> totalPriceColumn = ORDER_PRICE.field(1, BigDecimal.class);
        Field<Integer> itemCountColumn = ORDER_PRICE.field(2, Integer.class);

        Condition where = buildWhere(filter, totalPriceColumn);

        Mono<Long> count = doCount(dslContext.selectCount()
                .from(countTable)
                .where(where));

        return count.flatMap(total -> {
            if (total > 0) {
                return doSelect(dslContext.select(ORDER.asterisk(), totalPriceColumn, itemCountColumn)
                        .from(finalTable)
                        .where(where)
                        .orderBy(getSortFields(finalTable, pageable.getSort()))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset()), SummaryOrderDto.class)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));

            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    private Condition buildWhere(OrderFilter filter, Field<BigDecimal> totalPriceColumn) {
        Condition where = DSL.noCondition();
        where = where.and(ORDER.PUBLIC_POINT_ID.eq(filter.getPublicPointId()));
        where = where.and(ORDER.COMPANY_ID.eq(filter.getCompanyId()));
        if (StringUtils.isNotBlank(filter.getNumber())) {
            where = where.and(ORDER.NUMBER.equalIgnoreCase(filter.getNumber()));
        }
        if (filter.getFromCreatedDate() != null) {
            where = where.and(
                    ORDER.CREATED_DATE.greaterOrEqual(filter.getFromCreatedDate().atStartOfDay()));
        }
        if (filter.getToCreatedDate() != null) {
            LocalDateTime toDate = filter.getToCreatedDate().plusDays(1).atStartOfDay();
            where = where.and(ORDER.CREATED_DATE.lessThan(toDate));
        }
        if (filter.getStatus() != null) {
            where = where.and(ORDER.STATUS.in(List.of(filter.getStatus())));
        }
        if (filter.getDishId() != null) {
            where = where.and(ORDER_ITEM.DISH_ID.eq(filter.getDishId()));
        }
        if (filter.getFromTotalPrice() != null) {
            where = where.and(totalPriceColumn.greaterOrEqual(filter.getFromTotalPrice()));
        }
        if (filter.getToTotalPrice() != null) {
            where = where.and(totalPriceColumn.lessOrEqual(filter.getToTotalPrice()));
        }

        return where;
    }

    @Override
    public Flux<Order> findLiveOrders(LiveOrderFilter filter) {
        DSLContext dslContext = getDslContext();
        Condition where = buildWhere(filter);

        return doSelect(dslContext.selectFrom(ORDER)
                .where(where), Order.class);
    }

    private Condition buildWhere(LiveOrderFilter filter) {
        Condition where = DSL.noCondition();
        where = where.and(ORDER.PUBLIC_POINT_ID.eq(filter.getPublicPointId()));
        where = where.and(ORDER.COMPANY_ID.eq(filter.getCompanyId()));
        where = where.and(ORDER.STATUS.notIn(List.of(OrderStatus.DECLINED, OrderStatus.PAID)));

        return where;
    }
}
