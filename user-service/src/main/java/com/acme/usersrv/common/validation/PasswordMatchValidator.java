package com.acme.usersrv.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordHandler> {

    private String message;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PasswordHandler handler, ConstraintValidatorContext context) {
        boolean valid = Objects.equals(handler.getPassword(), handler.getConfirmPassword());

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addBeanNode()
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}