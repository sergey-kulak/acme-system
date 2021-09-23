package com.acme.commons.security;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Getter
@Setter
public class CompanyUser extends User implements CompanyUserDetails {
    private UUID id;
    private UUID companyId;
    private UUID publicPointId;
    private UserRole role;

    public CompanyUser(UUID id, UUID companyId,
                       String username, String password, UserRole role,
                       UUID publicPointId) {
        super(username, password, Collections.singletonList(new SimpleGrantedAuthority(role.toString())));
        this.id = id;
        this.companyId = companyId;
        this.publicPointId = publicPointId;
        this.role = role;
    }

    /**
     * Table client user
     *
     * @param id            - table id
     * @param companyId
     * @param publicPointId
     */
    public CompanyUser(UUID id, UUID companyId, UUID publicPointId) {
        super(id.toString(), StringUtils.EMPTY, Collections.singletonList(new SimpleGrantedAuthority("CLIENT")));
        this.id = id;
        this.companyId = companyId;
        this.publicPointId = publicPointId;
    }

    @Override
    public boolean hasAnyRole(UserRole... roles) {
        return role != null && Arrays.asList(roles).contains(role);
    }

    public UserRole getRole() {
        return role;
    }
}
