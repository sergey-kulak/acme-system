package com.acme.ppsrv.order.controller;

import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.OpenApiPage;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
import com.acme.ppsrv.order.dto.CreateOrderDto;
import com.acme.ppsrv.order.dto.LiveOrderFilter;
import com.acme.ppsrv.order.dto.OrderDto;
import com.acme.ppsrv.order.dto.OrderFilter;
import com.acme.ppsrv.order.dto.OrderItemStatusDto;
import com.acme.ppsrv.order.dto.OrderStatusDto;
import com.acme.ppsrv.order.dto.SummaryOrderDto;
import com.acme.ppsrv.order.service.OrderService;
import com.acme.ppsrv.publicpoint.dto.PublicPointStatusDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Api", description = "Order Management Api")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @SecureOperation(description = "Create an order")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreateOrderDto dto,
                                              ServerHttpRequest request) {
        return orderService.create(dto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping("{id}")
    @SecureOperation(description = "Find order by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<OrderDto> findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @GetMapping
    @SecureOperation(description = "Find orders with pagination")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = OrderApiPage.class)))
    public Mono<Page<SummaryOrderDto>> find(@ParameterObject OrderFilter filter,
                                            @ParameterObject Pageable pageable) {
        return orderService.find(filter, pageable);
    }

    @GetMapping("/live")
    @SecureOperation(description = "Find live orders")
    public Mono<List<OrderDto>> findLiveOrders(@ParameterObject LiveOrderFilter filter) {
        return orderService.findLiveOrders(filter);
    }

    @PutMapping("/{id}/status")
    @SecureOperation(description = "Change order status")
    @ApiResponse(responseCode = "400", description = "Not allowed status change", content = @Content(schema = @Schema(hidden = true)))
    @EntityNotFoundResponse
    public Mono<Void> changeStatus(@PathVariable UUID id, @RequestBody OrderStatusDto statusDto) {
        return orderService.changeStatus(id, statusDto.getStatus());
    }

    @PutMapping("/items/{id}/status")
    @SecureOperation(description = "Change order item status")
    @ApiResponse(responseCode = "400", description = "Not allowed status change", content = @Content(schema = @Schema(hidden = true)))
    @EntityNotFoundResponse
    public Mono<Void> changeStatus(@PathVariable UUID id, @RequestBody OrderItemStatusDto statusDto) {
        return orderService.changeStatus(id, statusDto.getStatus());
    }

    @Schema(name = "Summary order  page")
    private static class OrderApiPage extends OpenApiPage<SummaryOrderDto> {
    }
}
