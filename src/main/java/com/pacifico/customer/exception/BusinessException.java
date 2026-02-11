package com.pacifico.customer.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BusinessException extends RuntimeException {
    private HttpStatus httpStatus;
    private String id;
    private String code;
    private String type;
    private String description;
    private ErrorConstant.Category category;
    private List<String> errors;

    public BusinessException(String message) {
        super(message);
    }

    // Builder pattern para BusinessException
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BusinessException instance = new BusinessException("");
        public Builder httpStatus(HttpStatus httpStatus) { instance.httpStatus = httpStatus; return this; }
        public Builder id(String id) { instance.id = id; return this; }
        public Builder code(String code) { instance.code = code; return this; }
        public Builder type(String type) { instance.type = type; return this; }
        public Builder description(String description) { instance.description = description; return this; }
        public Builder category(ErrorConstant.Category category) { instance.category = category; return this; }
        public Builder errors(List<String> errors) { instance.errors = errors; return this; }
        public BusinessException build() { return instance; }
    }

    public static BusinessException createException(HttpStatus httpStatus, BusinessErrorCodes businessErrorCodes) {
        String code = getErrorCode(
                ErrorConstant.Type.SYSTEM,
                ErrorConstant.Layer.SUPPORT,
                ErrorConstant.SystemComponent.SUPPORT,
                businessErrorCodes.getCode());

        return BusinessException.builder()
                .httpStatus(httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus)
                .id(UUID.randomUUID().toString())
                .code(code)
                .type(ErrorConstant.Type.SYSTEM.getDescription())
                .description(businessErrorCodes.getDescription())
                .category(ErrorConstant.Category.ERROR)
                .errors(Collections.singletonList(businessErrorCodes.getTitle()))
                .build();
    }

    private static String getErrorCode(ErrorConstant.Type type, ErrorConstant.Layer layer, ErrorConstant.SystemComponent component, String code) {
        return String.format("%s-%s-%s-%s", type.name(), layer.name(), component.name(), code);
    }

    public String getDescription() {
        return description;
    }
    public java.util.List<String> getErrors() {
        return errors;
    }
}
