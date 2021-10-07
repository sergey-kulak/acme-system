package com.acme.ppsrv.publicpoint.event;

import com.acme.ppsrv.order.event.OrderStatusChangedEvent;
import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import com.acme.ppsrv.publicpoint.dto.NotificationEvent;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class NotificationConfig {
    @Autowired
    private FluxMessageChannel notificationChannel;

    @Bean
    public Function<Flux<NotificationDto>, Mono<Void>> ppNotification() {
        return flux -> flux
                .doOnNext(item -> notificationChannel.send(new GenericMessage<>(item)))
                .then();
    }
}
