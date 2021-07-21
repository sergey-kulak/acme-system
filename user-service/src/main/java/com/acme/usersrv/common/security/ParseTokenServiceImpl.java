package com.acme.usersrv.common.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParseTokenServiceImpl implements ParseTokenService {
    private static final String PEM_PRIVATE_KEY = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----\n";
    private static final Set<String> REQUIRED_CLAIMS = Set.of(JwtClaims.SUBJECT, JwtClaims.ISSUED_AT_CLAIM,
            JwtClaims.EXPIRATION_TIME, JwtClaims.ROLE);

    private final RSAKey rsaKey;
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @SneakyThrows
    public ParseTokenServiceImpl(String base64EncodedKey) {
        String pem = String.format(PEM_PRIVATE_KEY, base64EncodedKey);
        rsaKey = JWK.parseFromPEMEncodedObjects(pem).toRSAKey();
        jwtProcessor = new DefaultJWTProcessor<>();
        init();
    }

    private void init() {
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(JOSEObjectType.JWT));

        JWKSource<SecurityContext> keySource =
                (jwkSelector, context) -> Collections.singletonList(rsaKey);

        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(null, REQUIRED_CLAIMS));
    }

    @SneakyThrows
    @Override
    public Mono<Authentication> parseAccessToken(String accessToken) {
        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(accessToken, null);

            String username = claimsSet.getSubject();
            String role = claimsSet.getStringClaim(JwtClaims.ROLE);
            String companyId = claimsSet.getStringClaim(JwtClaims.COMPANY_ID);
            List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(role));

            CompanyUser user = new CompanyUser(
                    StringUtils.isBlank(companyId) ? null : UUID.fromString(companyId),
                    username, StringUtils.EMPTY, roles);

            return Mono.just(new UsernamePasswordAuthenticationToken(user, null, roles));
        } catch (Exception exception) {
            return Mono.error(new BadCredentialsException("Wrong token", exception));
        }
    }
}
