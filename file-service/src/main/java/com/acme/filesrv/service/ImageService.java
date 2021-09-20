package com.acme.filesrv.service;

import com.acme.filesrv.dto.DishImageUrlDto;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@Validated
public interface ImageService {
    Mono<Map<String, String>> getDishImageUrls(@Valid DishImageUrlDto dto);
}
