package com.acme.usersrv.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("\"user\"")
@Getter
@Setter
public class User {
    @Id
    private UUID id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String email;
    private String password;
    @Column("company_id")
    private UUID companyId;
    private UserStatus status;
    private UserRole role;
    private String phone;
}
