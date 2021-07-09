package com.acme.usersrv.common.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.acme.usersrv.common.utils.StreamUtils.mapToSet;

@Data
public class ValidationErrorDto {
    private static final String DEFAULT_ERROR_MSG = "Validation error";
    private String message;
    private Set<String> constraintViolations;

    public ValidationErrorDto(String message, Set<String> constraintViolations) {
        this.message = StringUtils.defaultIfBlank(message, DEFAULT_ERROR_MSG);
        this.constraintViolations = constraintViolations;
    }

    public ValidationErrorDto(ConstraintViolationException exception) {
        this.message = DEFAULT_ERROR_MSG;
        this.constraintViolations = mapToSet(exception.getConstraintViolations(), this::buildConstraintViolationText);
    }

    private String buildConstraintViolationText(ConstraintViolation<?> cv) {
        boolean classViolation = cv.getLeafBean() == cv.getInvalidValue();

        return classViolation ? cv.getMessage() : extractRealField(cv.getPropertyPath()) + " " + cv.getMessage();
    }

    private String extractRealField(Path propertyPath) {
        return StreamSupport.stream(propertyPath.spliterator(), false)
                .skip(2)
                .map(Objects::toString)
                .collect(Collectors.joining("."));
    }

}
