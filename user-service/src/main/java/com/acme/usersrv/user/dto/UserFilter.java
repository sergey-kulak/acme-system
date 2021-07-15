package com.acme.usersrv.user.dto;

import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User filter")
public class UserFilter {
    private String email;
    private Collection<UserStatus> statuses;
    private Collection<UserRole> roles;
    private UUID companyId;

    @Tolerate
    public UserFilter() {
    }
}
