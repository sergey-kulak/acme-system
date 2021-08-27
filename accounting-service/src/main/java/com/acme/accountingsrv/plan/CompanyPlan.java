package com.acme.accountingsrv.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("company_plan")
@Getter
@Setter
public class CompanyPlan {
    @Id
    private UUID id;
    @Column("company_id")
    private UUID companyId;
    @Column("plan_id")
    private UUID planId;
    @Column("start_date")
    private LocalDate startDate;
    @Column("end_date")
    private LocalDate endDate;
}
