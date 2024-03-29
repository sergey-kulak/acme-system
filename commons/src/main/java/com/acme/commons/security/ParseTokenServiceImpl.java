package com.acme.commons.security;

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
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Collections;
import java.util.Objects;
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
            UUID companyId = getUuid(claimsSet, JwtClaims.COMPANY_ID);
            UUID publicPointId = getUuid(claimsSet, JwtClaims.PUBLIC_POINT_ID);
            UUID id = UUID.fromString(claimsSet.getStringClaim(JwtClaims.ID));

            CompanyUser user = Objects.equals(role, SecurityUtils.CLIENT_AUTHORITY) ?
                    new CompanyUser(id, companyId, publicPointId) :
                    new CompanyUser(id, companyId, username,
                            StringUtils.EMPTY, UserRole.valueOf(role), publicPointId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(accessToken);
            return Mono.just(authentication);
        } catch (Exception exception) {
            return Mono.error(new BadCredentialsException("Wrong token", exception));
        }
    }

    private UUID getUuid(JWTClaimsSet claimsSet, String claim) throws ParseException {
        String text = claimsSet.getStringClaim(claim);
        return StringUtils.isBlank(text) ? null : UUID.fromString(text);
    }
}
