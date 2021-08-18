package com.acme.usersrv.common.utils;

import com.acme.commons.security.CompanyUserDetails;
import com.acme.commons.security.UserRole;
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

    public static Mono<Void> isCompanyAccessible(UUID companyId) {
        return getCurrentUser()
                .filter(cmpUser -> hasAccess(cmpUser, companyId))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")))
                .then();
    }

    private static boolean hasAccess(CompanyUserDetails cmpUser, UUID companyId) {
        return cmpUser.hasAnyRole(UserRole.ADMIN) || Objects.equals(companyId, cmpUser.getCompanyId());
    }

    public static Mono<UUID> hasCompanyAccess(UUID companyId) {
        return getCurrentUser()
                .map(cmpUser -> hasAccess(cmpUser, companyId))
                .filter(result -> result)
                .map(result -> companyId)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")));
    }

    public static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    public static Mono<CompanyUserDetails> getCurrentUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .cast(CompanyUserDetails.class);
    }

    public static Mono<UserRole> getCurrentUserRole() {
        return getCurrentUser()
                .map(CompanyUserDetails::getRole);
    }
}
