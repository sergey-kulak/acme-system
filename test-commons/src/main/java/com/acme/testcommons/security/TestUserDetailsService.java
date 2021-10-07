package com.acme.testcommons.security;

import com.acme.commons.security.CompanyUser;
import com.acme.commons.security.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestUserDetailsService implements UserDetailsService {
    private static final List<UserRole> PP_ROLES = Arrays.asList(UserRole.PP_MANAGER,
            UserRole.COOK, UserRole.WAITER);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String roleText = username.substring(0, username.indexOf("@")).toUpperCase();
        if (roleText.equals("CLIENT")) {
            return new CompanyUser(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        } else {
            UserRole role = UserRole.valueOf(roleText);
            UUID companyId = role == UserRole.ADMIN ? null : UUID.randomUUID();
            UUID publicPointId = PP_ROLES.contains(role) ? null : UUID.randomUUID();
            return new CompanyUser(UUID.randomUUID(), companyId, username.toLowerCase(),
                    "qwe123", role, publicPointId);
        }
    }
}
