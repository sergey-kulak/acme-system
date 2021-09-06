package com.acme.usersrv.user.dto;

import com.acme.commons.security.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User name filter")
public class UserNameFilter {
    @NotNull
    private UserRole role;
    @NotNull
    private UUID companyId;
    private UUID publicPointId;

    @Tolerate
    public UserNameFilter() {
    }
}
