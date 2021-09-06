package com.acme.ppsrv.publicpoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("public_point")
@Getter
@Setter
public class PublicPointTable {
    @Id
    private UUID id;
    private String name;
    @Column("public_point_id")
    private UUID publicPointId;
    @Column("seat_count")
    private int seatCount;
    private String description;
}
