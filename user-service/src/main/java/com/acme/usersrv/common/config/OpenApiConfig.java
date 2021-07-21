package com.acme.usersrv.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    static {
        SpringDocUtils.getConfig().replaceWithClass(org.springframework.data.domain.Pageable.class,
                org.springdoc.core.converters.models.Pageable.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT"))
                        .addSchemas("Login request", new Schema<>()
                                .addRequiredItem("username")
                                .addRequiredItem("password")
                                .addProperties("username", new Schema<>().type("string"))
                                .addProperties("password", new Schema<>().type("string"))
                        )
                        .addSchemas("Login response", new Schema<>()
                                .addProperties("accessToken", new Schema<>().type("string"))
                        )
                )
                .tags(List.of(new Tag()
                        .name("Auth Api")
                        .description("Authentication Api")
                ))
                .path("/api/auth/login", new PathItem()
                        .description("Login")
                        .post(new Operation()
                                .operationId("login")
                                .addTagsItem("Auth Api")
                                .description("Login")
                                .requestBody(new RequestBody()
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new Schema<>().$ref("#/components/schemas/Login request"))
                                                ))
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("OK")
                                                .content(new Content()
                                                        .addMediaType("application/json", new MediaType()
                                                                .schema(new Schema<>().$ref("#/components/schemas/Login response"))
                                                        )
                                                )

                                        )
                                )
                        )
                );
    }
}
