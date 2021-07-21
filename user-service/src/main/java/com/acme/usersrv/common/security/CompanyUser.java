package com.acme.usersrv.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class CompanyUser extends User implements CompanyUserDetails {
    private UUID companyId;

    public CompanyUser(UUID companyId, String username, String password,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.companyId = companyId;
    }

    @Override
    public UUID getCompanyId() {
        return companyId;
    }
}
