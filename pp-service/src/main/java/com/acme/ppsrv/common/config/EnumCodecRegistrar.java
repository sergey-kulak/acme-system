package com.acme.ppsrv.common.config;

import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.codec.CodecRegistry;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import org.reactivestreams.Publisher;

public class EnumCodecRegistrar implements CodecRegistrar {

    @Override
    public Publisher<Void> register(PostgresqlConnection connection,
                                    ByteBufAllocator allocator,
                                    CodecRegistry registry) {
        return EnumCodec.builder()
                .withEnum("public_point_status", PublicPointStatus.class)
                .withEnum("order_status", OrderStatus.class)
                .withEnum("order_item_status", OrderItemStatus.class)
                .build()
                .register(connection, allocator, registry);
    }
}
