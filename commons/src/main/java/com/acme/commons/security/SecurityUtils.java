package com.acme.commons.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class SecurityUtils {
    private static final UUID BG_ADMIN_ID = UUID.fromString("d4d742b0-26f8-4440-970b-5182a0744217");
    private static final String BG_ADMIN_EMAIL = "bg-admin@acme.com";

    private static final UserRole[] FULL_COMPANY_ACCESS = new UserRole[]{UserRole.ADMIN};
    private static final UserRole[] FULL_COMPANY_ACCOUNTING_ACCESS =
            new UserRole[]{UserRole.ADMIN, UserRole.ACCOUNTANT};

    private SecurityUtils() {
    }

    public static Mono<Void> isCompanyAccessible(UUID companyId) {
        return isCompanyAccessible(companyId, false);
    }

    public static Mono<Void> isCompanyAccessible(UUID companyId, boolean accountingCtx) {
        return getCurrentUser()
                .filter(cmpUser -> hasAccess(cmpUser, companyId, accountingCtx))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")))
                .then();
    }

    private static boolean hasAccess(CompanyUserDetails cmpUser, UUID companyId, boolean accountingCtx) {
        UserRole[] allAccessRole = accountingCtx ? FULL_COMPANY_ACCOUNTING_ACCESS : FULL_COMPANY_ACCESS;
        return cmpUser.hasAnyRole(allAccessRole) || Objects.equals(companyId, cmpUser.getCompanyId());
    }

    public static Mono<UUID> hasCompanyAccess(UUID companyId) {
        return getCurrentUser()
                .filter(cmpUser -> hasAccess(cmpUser, companyId, false))
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

    public static Function<Context, Context> withBgAdmin() {
        CompanyUser user = new CompanyUser(BG_ADMIN_ID, null, BG_ADMIN_EMAIL,
                StringUtils.EMPTY, UserRole.ADMIN, null);

        Context context = ReactiveSecurityContextHolder
                .withAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        return (ctx) -> context;
    }
}
