package com.acme.testcommons.security;

import com.acme.commons.security.CompanyUser;
import com.acme.commons.security.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public class TestUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String roleText = username.substring(0, username.indexOf("@"));
        UserRole role = UserRole.valueOf(roleText.toUpperCase());
        UUID companyId = role == UserRole.ADMIN ? null : UUID.randomUUID();
        return new CompanyUser(UUID.randomUUID(), companyId, username.toLowerCase(), "qwe123", role);
    }
}
