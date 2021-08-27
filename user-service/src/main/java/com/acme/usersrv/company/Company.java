package com.acme.usersrv.company;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("company")
@Getter
@Setter
public class Company {
    @Id
    private UUID id;
    @Column("full_name")
    private String fullName;
    private String vatin;
    @Column("reg_number")
    private String regNumber;
    private String email;
    private String country;
    private String city;
    private String address;
    private String site;
    private String phone;
    private CompanyStatus status;
}
