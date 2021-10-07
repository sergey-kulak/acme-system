package com.acme.ppsrv.order.mapper;

import com.acme.commons.mapper.StringMapper;
import com.acme.ppsrv.order.Order;
import com.acme.ppsrv.order.OrderItem;
import com.acme.ppsrv.order.dto.CreateOrderItemDto;
import com.acme.ppsrv.order.dto.OrderDto;
import com.acme.ppsrv.order.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StringMapper.class})
public interface OrderMapper {
    OrderItem fromDto(CreateOrderItemDto source);

    OrderItemDto toDto(OrderItem source);

    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order, List<OrderItem> items);
}
