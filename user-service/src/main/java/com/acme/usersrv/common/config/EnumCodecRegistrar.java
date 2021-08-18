package com.acme.usersrv.common.config;

import com.acme.usersrv.company.CompanyStatus;
import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.UserStatus;
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
                .withEnum("user_status", UserStatus.class)
                .withEnum("user_role", UserRole.class)
                .withEnum("company_status", CompanyStatus.class)
                .build()
                .register(connection, allocator, registry);
    }
}
