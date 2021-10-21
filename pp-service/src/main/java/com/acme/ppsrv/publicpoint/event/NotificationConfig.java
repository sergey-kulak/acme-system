package com.acme.ppsrv.publicpoint.event;

import com.acme.ppsrv.publicpoint.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
public class NotificationConfig {
    @Autowired
    private FluxMessageChannel notificationChannel;

    @Bean
    public Function<Flux<NotificationDto>, Mono<Void>> ppNotification() {
        return flux -> flux
                .doOnNext(this::republish)
                .then();
    }

    private void republish(NotificationDto item) {
        try {
            notificationChannel.send(new GenericMessage<>(item));
        } catch (Exception e) {
            if (isNoSubscribersException(e)) {
                log.warn("message was skipped because of no subscribers");
            } else {
                log.error("error during re-publish notification", e);
            }
        }
    }

    private boolean isNoSubscribersException(Exception exception) {
        Throwable cause = exception.getCause();
        return exception instanceof MessageDeliveryException &&
                cause instanceof IllegalStateException &&
                cause.getMessage().contains("doesn't have subscribers");

    }
}
