package com.acme.usersrv.company.dto;

import com.acme.usersrv.common.validation.PasswordHandler;
import com.acme.usersrv.common.validation.PasswordMatch;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@PasswordMatch
public class SaveOwnerDto implements PasswordHandler {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    private String phone;
    @Size(min = 6)
    private String password;
    private String confirmPassword;

    @Tolerate
    public SaveOwnerDto() {
    }
}
