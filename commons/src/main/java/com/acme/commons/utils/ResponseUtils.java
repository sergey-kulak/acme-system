package com.acme.commons.utils;

import com.acme.commons.dto.IdDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

public class ResponseUtils {
    private ResponseUtils() {
    }

    public static ResponseEntity<IdDto> buildCreatedResponse(ServerHttpRequest request, UUID id) {
        return ResponseEntity.created(UriComponentsBuilder
                .fromHttpRequest(request)
                .path("/{id}")
                .buildAndExpand(id)
                .toUri())
                .body(new IdDto(id));
    }
}
