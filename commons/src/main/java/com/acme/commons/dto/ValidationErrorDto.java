package com.acme.commons.dto;

import com.acme.commons.utils.StreamUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Set;


@Data
public class ValidationErrorDto {
    private static final String DEFAULT_ERROR_MSG = "Validation error";
    private static final int MAX_ROOT_LEVEL = 2;
    private String message;
    private Set<String> constraintViolations;

    public ValidationErrorDto(String message, Set<String> constraintViolations) {
        this.message = StringUtils.defaultIfBlank(message, DEFAULT_ERROR_MSG);
        this.constraintViolations = constraintViolations;
    }

    public ValidationErrorDto(ConstraintViolationException exception) {
        this.message = DEFAULT_ERROR_MSG;
        this.constraintViolations = StreamUtils.mapToSet(exception.getConstraintViolations(), this::buildConstraintViolationText);
    }

    private String buildConstraintViolationText(ConstraintViolation<?> cv) {
        boolean classViolation = cv.getLeafBean() == cv.getInvalidValue();

        return classViolation ? cv.getMessage() : extractRealField(cv.getPropertyPath()) + " " + cv.getMessage();
    }

    private String extractRealField(Path propertyPath) {
        return removeFirstPart(propertyPath.toString(), 0);
    }

    private String removeFirstPart(String path, int level) {
        int dotPos = path.indexOf(".");
        return dotPos > 0 & level < MAX_ROOT_LEVEL ?
                removeFirstPart(path.substring(dotPos + 1), level + 1) : path;
    }

}
