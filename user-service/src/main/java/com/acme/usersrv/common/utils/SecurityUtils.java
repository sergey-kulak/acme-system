package com.acme.usersrv.common.utils;

import com.acme.usersrv.common.security.CompanyUserDetails;
import com.acme.usersrv.user.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static Mono<UUID> hasCompanyAccess(UUID companyId) {
        return getCurrentUser()
                .map(cmpUser ->
                        cmpUser.hasAnyRole(UserRole.ADMIN) || Objects.equals(companyId, cmpUser.getCompanyId()))
                .filter(result -> result)
                .map(result -> companyId)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")));
    }

    public static Mono<CompanyUserDetails> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(CompanyUserDetails.class);
    }

    public static Mono<UserRole> getCurrentUserRole() {
        return getCurrentUser()
                .map(CompanyUserDetails::getRole);
    }
}
