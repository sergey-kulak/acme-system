package com.acme.ppsrv.order.repository;

import com.acme.ppsrv.order.Order;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import java.util.UUID;

public interface OrderRepository extends ReactiveSortingRepository<Order, UUID>, OrderRepositoryCustom {
}
