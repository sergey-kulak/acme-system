package com.acme.ppsrv.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("\"order\"")
@Getter
@Setter
public class Order {
    @Id
    private UUID id;
    @Column("company_id")
    private UUID companyId;
    @Column("public_point_id")
    private UUID publicPointId;
    @Column("table_id")
    private UUID tableId;
    private String number;
    private OrderStatus status;
    @Column("created_date")
    private Instant createdDate;
    @Column("paid_date")
    private Instant paidDate;

}
