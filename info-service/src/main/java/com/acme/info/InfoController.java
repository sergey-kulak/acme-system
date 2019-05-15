package com.acme.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class InfoController {
    @Value("${acme.app.version}")
    private String appVersion;

    @Value("${acme.app.name}")
    private String appName;

    @GetMapping("/welcome")
    public String welcome() {
        return String.format("%s %s", appName, appVersion);
    }
}

