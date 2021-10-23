package com.acme.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class InfoController {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);

    @Value("${acme.app.version}")
    private String appVersion;

    @Value("${acme.app.name}")
    private String appName;

    @Value("${spring.application.id}")
    private String appId;

    @GetMapping("")
    public String welcome() {
        log.info("providing info");
        return String.format("%s %s, id: %s", appName, appVersion, appId);
    }
}

