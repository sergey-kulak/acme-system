/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme.apigw.config;

import com.acme.apigw.discovery.DiscoveryRouteDefinitionLocator;
import com.acme.apigw.discovery.DiscoveryRouteProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cloud.gateway.enabled", matchIfMissing = true)
public class DiscoveryRouteConfiguration {

    @Bean
    public DiscoveryRouteProperties discoveryRouteProperties() {
        DiscoveryRouteProperties properties = new DiscoveryRouteProperties();
        return properties;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = "spring.cloud.discovery.reactive.enabled", matchIfMissing = true)
    public static class ReactiveDiscoveryClientRouteDefinitionLocatorConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.cloud.gateway.discovery.locator.enabled")
        public DiscoveryRouteDefinitionLocator discoveryRouteDefinitionLocator(
                ReactiveDiscoveryClient discoveryClient,
                DiscoveryLocatorProperties properties,
                DiscoveryRouteProperties routeProperties) {
            return new DiscoveryRouteDefinitionLocator(discoveryClient, properties,
                    routeProperties.getRoutes());
        }

    }

}
