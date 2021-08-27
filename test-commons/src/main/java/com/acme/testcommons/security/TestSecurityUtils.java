package com.acme.testcommons.security;

import com.acme.commons.security.CompanyUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class TestSecurityUtils {
    private TestSecurityUtils() {
    }

    public static Mono<Void> linkWithCurrentUser(UUID companyId) {
        return getCurrentUser()
                .doOnSuccess(cmpUser -> cmpUser.setCompanyId(companyId))
                .then();
    }

    public static Mono<CompanyUser> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(CompanyUser.class);
    }

    public static Mono<UUID> linkOtherCompanyWithCurrentUser() {
        return getCurrentUser()
                .map(cmpUser -> {
                    cmpUser.setCompanyId(UUID.randomUUID());
                    return cmpUser.getCompanyId();
                });
    }
}
