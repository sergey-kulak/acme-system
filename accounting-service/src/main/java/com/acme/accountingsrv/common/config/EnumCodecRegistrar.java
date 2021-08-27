package com.acme.accountingsrv.common.config;

import com.acme.accountingsrv.plan.PlanStatus;
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
                .withEnum("plan_status", PlanStatus.class)
                .build()
                .register(connection, allocator, registry);
    }
}
