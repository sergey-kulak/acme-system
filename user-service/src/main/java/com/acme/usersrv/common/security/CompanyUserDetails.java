package com.acme.usersrv.common.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface CompanyUserDetails extends UserDetails {
    UUID getCompanyId();
}
