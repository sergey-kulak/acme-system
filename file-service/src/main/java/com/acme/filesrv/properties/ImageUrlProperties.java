package com.acme.filesrv.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file-srv.image")
public class ImageUrlProperties {
    private String subPath;
    private int uploadExpiration;
    private int downloadExpiration;
}
