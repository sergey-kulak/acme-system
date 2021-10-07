package com.acme.ppsrv.publicpoint.service.impl;

import com.acme.commons.security.CompanyUser;
import com.acme.commons.security.TokenService;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.ClientLoginRequest;
import com.acme.ppsrv.publicpoint.dto.ClientLoginResponse;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.ppsrv.publicpoint.service.PublicPointTableLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PublicPointTableLoginServiceImpl implements PublicPointTableLoginService {
    private final PublicPointTableRepository ppTableRepository;
    private final PublicPointRepository ppRepository;
    private final TokenService tokenService;

    @Override
    public Mono<ClientLoginResponse> login(ClientLoginRequest request) {
        return ppTableRepository.findByCode(request.getCode())
                .zipWhen(ppTable -> ppRepository.findById(ppTable.getPublicPointId()))
                .map(data -> map(data.getT1(), data.getT2()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")));
    }

    private ClientLoginResponse map(PublicPointTable ppTable, PublicPoint pp) {
        return ClientLoginResponse.builder()
                .publicPointName(pp.getName())
                .currency(pp.getCurrency())
                .accessToken(generateToken(ppTable, pp))
                .build();
    }

    private String generateToken(PublicPointTable ppTable, PublicPoint pp) {
        CompanyUser companyUser = new CompanyUser(ppTable.getId(), pp.getCompanyId(),
                pp.getId());

        return tokenService.generateAccessToken(new ClientAuthenticationToken(companyUser));
    }

    private static class ClientAuthenticationToken extends AbstractAuthenticationToken {
        private Object principal;

        public ClientAuthenticationToken(CompanyUser companyUser) {
            super(companyUser.getAuthorities());
            this.principal = companyUser;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }
    }
}
