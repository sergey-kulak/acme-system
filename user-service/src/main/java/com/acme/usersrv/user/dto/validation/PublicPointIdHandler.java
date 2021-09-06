package com.acme.usersrv.user.dto.validation;


import com.acme.commons.security.UserRole;

import java.util.UUID;

public interface PublicPointIdHandler {
    UserRole getRole();

    UUID getPublicPointId();
}
