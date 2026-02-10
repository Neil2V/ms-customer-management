package com.pacifico.customer.exception;

import com.pacifico.customer.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler({ NoResourceFoundException.class, NotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {

        log.error("Recurso no encontrado", ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "NOT_FOUND",
                        ex.getMessage(),
                        null,
                        LocalDateTime.now()
                ));
    }

    // 400 - Errores de negocio

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

        log.error("Error de negocio", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "BUSINESS_ERROR",
                        ex.getDescription() != null ? ex.getDescription() : ex.getMessage(),
                        ex.getErrors() != null && !ex.getErrors().isEmpty() ? Map.of("field", ex.getErrors().get(0)) : null,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(WebExchangeBindException ex) {

        log.error("Error de validación", ex);

        Map<String, String> fieldErrors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Objects.requireNonNullElse(
                                fe.getDefaultMessage(),
                                "Valor inválido"
                        ),
                        (m1, m2) -> m1
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "VALIDATION_ERROR",
                        "Error de validación en la solicitud",
                        fieldErrors,
                        LocalDateTime.now()
                ));
    }

    // 500 - Errores técnicos

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        log.error("Error técnico no controlado", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "Ocurrió un error inesperado",
                        null,
                        LocalDateTime.now()
                ));
    }
}