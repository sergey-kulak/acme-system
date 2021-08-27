package com.acme.accountingsrv.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("plan_country")
@Getter
@Setter
public class PlanCountry {
    @Column("plan_id")
    private UUID planId;
    private String country;
}
