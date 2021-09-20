package com.acme.filesrv.controller;

import com.acme.filesrv.dto.DishImageUrlDto;
import com.acme.filesrv.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image Api", description = "Image Management Api")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/dish")
    public Mono<Map<String, String>> getDishImageUrls(@RequestBody DishImageUrlDto dto) {
        return imageService.getDishImageUrls(dto);
    }
}
