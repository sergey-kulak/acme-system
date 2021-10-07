package com.acme.ppsrv.common.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;

import java.util.Map;

@Configuration
public class RSocketConfig implements InitializingBean {
    @Autowired
    private RSocketStrategies rSocketStrategies;

    @Bean
    public FluxMessageChannel notificationChannel() {
        return new FluxMessageChannel();
    }

    @Override
    public void afterPropertiesSet() {
        MetadataExtractor metadataExtractor = rSocketStrategies.metadataExtractor();
        if (metadataExtractor instanceof DefaultMetadataExtractor) {
            ((DefaultMetadataExtractor) metadataExtractor).metadataToExtract(
                    MimeType.valueOf("application/json"),
                    new ParameterizedTypeReference<Map<String, String>>() {
                    },
                    (jsonMap, outputMap) -> {
                        outputMap.putAll(jsonMap);
                    });
        }
    }

}
