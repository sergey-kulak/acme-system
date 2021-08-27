package com.acme.usersrv.user.dto.validation;

import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.dto.CreateUserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CompanyIdRoleCheckValidator implements ConstraintValidator<CompanyIdRoleCheck, CreateUserDto> {
    private static final List<UserRole> NO_COMPANY_ROLES = Arrays.asList(UserRole.ADMIN, UserRole.ACCOUNTANT);
    private static final String NO_COMPANY_ID_MESSAGE = "Company id must be empty";
    private static final String COMPANY_ID_MESSAGE = "Company id must be set";

    @Override
    public void initialize(CompanyIdRoleCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateUserDto dto, ConstraintValidatorContext context) {
        String message;
        boolean valid;

        if (NO_COMPANY_ROLES.contains(dto.getRole())) {
            valid = dto.getCompanyId() == null;
            message = NO_COMPANY_ID_MESSAGE;
        } else {
            valid = dto.getCompanyId() != null;
            message = COMPANY_ID_MESSAGE;
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addBeanNode()
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
