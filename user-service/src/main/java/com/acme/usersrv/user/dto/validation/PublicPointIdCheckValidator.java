package com.acme.usersrv.user.dto.validation;

import com.acme.commons.security.UserRole;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PublicPointIdCheckValidator implements ConstraintValidator<PublicPointIdCheck, PublicPointIdHandler> {
    private static final List<UserRole> PP_ROLES = Arrays.asList(UserRole.PP_MANAGER,
            UserRole.COOK, UserRole.WAITER);
    private static final String PP_ID_MESSAGE = "Public point id must be set";

    @Override
    public void initialize(PublicPointIdCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PublicPointIdHandler dto, ConstraintValidatorContext context) {
        boolean valid = true;

        if (dto.getRole() != null && PP_ROLES.contains(dto.getRole())) {
            valid = dto.getPublicPointId() != null;
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(PP_ID_MESSAGE)
                    .addBeanNode()
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
