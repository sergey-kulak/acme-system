package com.acme.usersrv.info.controller;

import com.acme.usersrv.info.api.InfoApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
@Slf4j
public class InfoController {
    private final InfoApi infoApi;

    @GetMapping
    public Mono<String> getInfo() {
        log.info("getting info");
        return infoApi.getInfo();
    }
}
