package com.acme.filesrv.config;

import com.acme.filesrv.properties.ImageUrlProperties;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private ImageUrlProperties urlProperties;

    @Bean(name = "download-image-url-cache")
    public org.infinispan.configuration.cache.Configuration downloadImageUrlCache() {
        return buildCache(urlProperties.getDownloadExpiration());
    }

    @Bean(name = "upload-image-url-cache")
    public org.infinispan.configuration.cache.Configuration uploadImageUrlCache() {
        return buildCache(urlProperties.getUploadExpiration());
    }

    private org.infinispan.configuration.cache.Configuration buildCache(int expirationInMin) {
        return new ConfigurationBuilder()
                .memory()
                .expiration()
                .maxIdle(expirationInMin * 60 * 1000L)
                .build();
    }

}
