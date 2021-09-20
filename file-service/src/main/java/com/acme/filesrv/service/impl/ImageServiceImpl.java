package com.acme.filesrv.service.impl;

import com.acme.commons.security.SecurityUtils;
import com.acme.filesrv.Action;
import com.acme.filesrv.dto.DishImageUrlDto;
import com.acme.filesrv.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.acme.filesrv.properties.ImageUrlProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    private static final String CACHE_PREFIX = "-image-url-cache";

    private final CacheManager cacheManager;
    private final ImageUrlProperties imageProperties;
    private final S3Presigner presigner = S3Presigner.builder().build();
    @Value("${file-srv.bucket}")
    private String bucketName;

    @Override
    public Mono<Map<String, String>> getDishImageUrls(DishImageUrlDto dto) {
        return checkAccess(dto.getCompanyId(), dto.getPublicPointId(), dto.getAction())
                .then(buildDishUrls(dto));
    }

    private Mono<Map<String, String>> buildDishUrls(DishImageUrlDto dto) {
        Action action = dto.getAction();
        Cache cache = cacheManager.getCache(action.name().toLowerCase() + CACHE_PREFIX);
        Map<String, String> resultMap = new HashMap<>();
        for (String imageKey : dto.getImageKeys()) {
            String url = buildImageUrl(dto.getCompanyId(), dto.getPublicPointId(), imageKey);
            String signUrl = cache.get(url, () -> sign(url, dto.getAction()));
            resultMap.put(imageKey, signUrl);
        }

        return Mono.just(resultMap);
    }

    private String buildImageUrl(UUID companyId, UUID ppId, String imageKey) {
        return String.format("%s/%s/%s/dish/%s", imageProperties.getSubPath(),
                companyId.toString(), ppId.toString(), imageKey);
    }

    private String sign(String objectKey, Action action) {
        log.info("sign {} for {}", objectKey, action);
        return action == Action.UPLOAD ?
                signUpload(objectKey) : signDownload(objectKey);
    }

    private String signDownload(String objectKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest singRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(imageProperties.getDownloadExpiration()))
                .build();

        return presigner.presignGetObject(singRequest)
                .url().toString();
    }

    private String signUpload(String objectKey) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectPresignRequest singRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(imageProperties.getUploadExpiration()))
                .build();

        return presigner.presignPutObject(singRequest)
                .url().toString();
    }

    private Mono<Void> checkAccess(UUID companyId, UUID ppId, Action action) {
        return action == Action.UPLOAD ?
                SecurityUtils.isPpAccessible(companyId, ppId) : Mono.empty();
    }
}
