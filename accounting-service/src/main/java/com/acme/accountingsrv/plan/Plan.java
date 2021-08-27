package com.acme.accountingsrv.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("plan")
@Getter
@Setter
public class Plan {
    @Id
    private UUID id;
    private String name;
    private String description;
    private PlanStatus status;
    @Column("max_table_count")
    private int maxTableCount;
    @Column("month_price")
    private BigDecimal monthPrice;
    @Column("currency")
    private String currency;
    @Column("upfront_discount_6m")
    private BigDecimal upfrontDiscount6m;
    @Column("upfront_discount_1y")
    private BigDecimal upfrontDiscount1y;

}
