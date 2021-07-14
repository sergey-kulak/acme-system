package com.acme.usersrv.common.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.jooq.AttachableQueryPart;
import org.jooq.DSLContext;
import org.jooq.EnumType;
import org.jooq.Param;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.conf.ParamType;
import org.jooq.impl.TableImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AbstractCustomJooqRepository extends AbstractCustomR2dbcRepository {
    private DSLContext dslContext;

    public Mono<Long> doCount(AttachableQueryPart query) {
        return getSpec(query)
                .map((r, md) -> r.get(0, Long.class))
                .first()
                .defaultIfEmpty(0L);
    }

    private DatabaseClient.GenericExecuteSpec getSpec(AttachableQueryPart query) {
        String sql = query.getSQL(ParamType.NAMED);
        Map<String, Param<?>> bindObjects = query.getParams();

        DatabaseClient.GenericExecuteSpec spec = getEntityOperations().getDatabaseClient().sql(sql);
        for (Map.Entry<String, Param<?>> entry : bindObjects.entrySet()) {
            Object value = entry.getValue().getValue();
            value = value instanceof EnumType ? ((EnumType) value).getLiteral() : value;
            spec = spec.bind(entry.getKey(), value);
        }
        return spec;
    }

    public <T> Flux<T> doSelect(AttachableQueryPart query, Class<T> returnType) {
        BiFunction<Row, RowMetadata, T> rowMapper = getEntityOperations().getDataAccessStrategy().getRowMapper(returnType);
        return getSpec(query)
                .map(rowMapper)
                .all();
    }

    public Collection<SortField<?>> getSortFields(TableImpl<?> table, Sort sort) {
        return sort == null ? Collections.emptyList() :
                sort.stream()
                        .map(order -> toSortField(table, order))
                        .collect(Collectors.toList());
    }

    private SortField<?> toSortField(TableImpl<?> table, Sort.Order order) {
        String fieldName = order.getProperty();
        Sort.Direction sortDirection = order.getDirection();

        return table.fieldsRow()
                .field(fieldName)
                .sort(sortDirection == Sort.Direction.ASC ? SortOrder.ASC : SortOrder.ASC);
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

    @Autowired
    public void setDslContext(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
}
