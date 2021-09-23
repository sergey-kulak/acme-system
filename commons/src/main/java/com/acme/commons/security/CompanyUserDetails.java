package com.acme.commons.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface CompanyUserDetails extends UserDetails {
    UUID getId();

    UUID getCompanyId();

    boolean hasAnyRole(UserRole... role);

    UUID getPublicPointId();
}
