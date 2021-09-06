package com.acme.usersrv.common.security;

import com.acme.commons.security.CompanyUser;
import com.acme.usersrv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findActiveByEmail(username)
                .map(appUser -> new CompanyUser(appUser.getId(), appUser.getCompanyId(),
                        appUser.getEmail(), appUser.getPassword(), appUser.getRole(),
                        appUser.getPublicPointId()));
    }
}
