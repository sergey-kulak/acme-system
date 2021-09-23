package com.acme.ppsrv.publicpoint.service;

import com.acme.ppsrv.publicpoint.dto.ClientLoginRequest;
import com.acme.ppsrv.publicpoint.dto.ClientLoginResponse;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface PublicPointTableLoginService {

    Mono<ClientLoginResponse> login(@Valid ClientLoginRequest request);
}
