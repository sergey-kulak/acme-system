package com.acme.commons.security;

import com.acme.commons.utils.CollectionUtils;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.UUID;

public class TokenServiceImpl implements TokenService {
    private static final String PEM_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n%s\n-----END PRIVATE KEY-----\n";
    private static final JWSAlgorithm DEFAULT_JWS_ALG = JWSAlgorithm.RS256;

    private final RSAKey rsaKey;
    private final long ttlInSec;

    @SneakyThrows
    public TokenServiceImpl(String base64EncodedKey, long ttlInSec) {
        String pem = String.format(PEM_PRIVATE_KEY, base64EncodedKey);
        rsaKey = JWK.parseFromPEMEncodedObjects(pem).toRSAKey();
        this.ttlInSec = ttlInSec;
    }

    @Override
    @SneakyThrows
    public String generateAccessToken(Authentication authentication) {
        JWSSigner signer = new RSASSASigner(rsaKey);
        JWTClaimsSet claimsSet = buildClaims(authentication);
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(DEFAULT_JWS_ALG)
                        .type(JOSEObjectType.JWT)
                        .keyID(rsaKey.getKeyID())
                        .build(),
                claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private JWTClaimsSet buildClaims(Authentication authentication) {
        CompanyUserDetails userDetails = (CompanyUserDetails) authentication.getPrincipal();
        String role = CollectionUtils.getFirst(userDetails.getAuthorities()).toString();
        UUID companyId = userDetails.getCompanyId();
        UUID ppId = userDetails.getPublicPointId();

        return new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + ttlInSec * 1000))
                .claim(JwtClaims.ID, userDetails.getId().toString())
                .claim(JwtClaims.ROLE, role)
                .claim(JwtClaims.COMPANY_ID, companyId == null ? null : companyId.toString())
                .claim(JwtClaims.PUBLIC_POINT_ID, ppId == null ? null : ppId.toString())
                .build();
    }

}
