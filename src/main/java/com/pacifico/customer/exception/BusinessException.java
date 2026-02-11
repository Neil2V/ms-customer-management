package com.pacifico.customer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String id;
    private final String code;
    private final String type;
    private final String description;
    private final ErrorConstant.Category category;
    private final List<String> errors;

    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.id = UUID.randomUUID().toString();
        this.code = null;
        this.type = null;
        this.description = message;
        this.category = ErrorConstant.Category.ERROR;
        this.errors = Collections.singletonList(message);
    }

    public BusinessException(Builder builder) {
        super(builder.description);
        this.httpStatus = builder.httpStatus;
        this.id = builder.id;
        this.code = builder.code;
        this.type = builder.type;
        this.description = builder.description;
        this.category = builder.category;
        this.errors = builder.errors;
    }

    public static BusinessException createException(HttpStatus httpStatus,
                                                    BusinessErrorCodes businessErrorCodes) {

        String code = getErrorCode(
                ErrorConstant.Type.SYSTEM,
                ErrorConstant.Layer.SUPPORT,
                ErrorConstant.SystemComponent.SUPPORT,
                businessErrorCodes.getCode());

        return new BusinessException.Builder()
                .httpStatus(httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus)
                .code(code)
                .type(ErrorConstant.Type.SYSTEM.getDescription())
                .description(businessErrorCodes.getDescription())
                .category(ErrorConstant.Category.ERROR)
                .errors(Collections.singletonList(businessErrorCodes.getTitle()))
                .build();
    }

    private static String getErrorCode(ErrorConstant.Type type,
                                       ErrorConstant.Layer layer,
                                       ErrorConstant.SystemComponent component,
                                       String code) {
        return String.format("%s-%s-%s-%s", type.name(), layer.name(), component.name(), code);
    }

    public static class Builder {
        private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        private String id = UUID.randomUUID().toString();
        private String code;
        private String type;
        private String description;
        private ErrorConstant.Category category;
        private List<String> errors = Collections.emptyList();

        public Builder httpStatus(HttpStatus httpStatus) { this.httpStatus = httpStatus; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(ErrorConstant.Category category) { this.category = category; return this; }
        public Builder errors(List<String> errors) { this.errors = errors; return this; }

        public BusinessException build() {
            return new BusinessException(this);
        }
    }
}
