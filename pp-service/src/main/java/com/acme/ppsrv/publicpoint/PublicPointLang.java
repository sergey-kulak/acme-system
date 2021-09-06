package com.acme.ppsrv.publicpoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("public_point_lang")
@Getter
@Setter
public class PublicPointLang {
    @Column("public_point_id")
    private UUID publicPointId;
    private String lang;
}
