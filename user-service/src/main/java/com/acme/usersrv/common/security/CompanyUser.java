package com.acme.usersrv.common.security;

import com.acme.usersrv.common.utils.CollectionUtils;
import com.acme.usersrv.user.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class CompanyUser extends User implements CompanyUserDetails {
    @Getter
    @Setter
    private UUID id;
    @Getter
    @Setter
    private UUID companyId;

    public CompanyUser(UUID id, UUID companyId,
                       String username, String password, UserRole role) {
        super(username, password, Collections.singletonList(new SimpleGrantedAuthority(role.toString())));
        this.id = id;
        this.companyId = companyId;
    }

    @Override
    public boolean hasAnyRole(UserRole... roles) {
        return Arrays.asList(roles).contains(getRole());
    }

    @Override
    public UserRole getRole() {
        String authority = CollectionUtils.getFirst(getAuthorities()).getAuthority();
        return UserRole.valueOf(authority);
    }
}
