package com.acme.ppsrv.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("order_item")
@Getter
@Setter
public class OrderItem {
    @Id
    private UUID id;
    @Column("order_id")
    private UUID orderId;
    private OrderItemStatus status;
    @Column("dish_id")
    private UUID dishId;
    @Column("dish_name")
    private String dishName;
    private BigDecimal price;
    private int quantity;
    @Column("created_date")
    private Instant createdDate;
    @Column("done_date")
    private Instant doneDate;
    private String comment;
}
