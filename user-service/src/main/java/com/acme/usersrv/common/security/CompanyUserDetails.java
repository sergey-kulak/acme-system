package com.acme.usersrv.common.security;

import com.acme.usersrv.user.UserRole;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface CompanyUserDetails extends UserDetails {
    UUID getId();

    UUID getCompanyId();

    boolean hasAnyRole(UserRole... role);

    UserRole getRole();
}
