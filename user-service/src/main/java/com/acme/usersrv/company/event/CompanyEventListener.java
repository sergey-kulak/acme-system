package com.acme.usersrv.company.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyEventListener {
    private final StreamBridge streamBridge;

    @EventListener
    public void onEvent(CompanyRegisteredEvent event) {
        log.info("publish company registration event: {}", event);
        streamBridge.send("company-registration", event);
    }
}
