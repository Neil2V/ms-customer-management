package com.pacifico.customer.exception;

import com.pacifico.customer.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Errores de negocio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

        log.error(this.getClass().getSimpleName(), ex);

        ErrorResponse error = new ErrorResponse(
                "BUSINESS_ERROR",
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Errores de validación
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(WebExchangeBindException ex) {

        log.error(this.getClass().getSimpleName(), ex);

        Map<String, String> fieldErrors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Objects.requireNonNullElse(
                                fe.getDefaultMessage(),
                                "Valor inválido"
                        ),
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "Error de validación en la solicitud",
                fieldErrors,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Errores técnicos no controlados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        log.error(this.getClass().getSimpleName(), ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Ocurrió un error inesperado",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    // Error de recurso no encontrado
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {

        log.error(this.getClass().getSimpleName(), ex);

        ErrorResponse error = new ErrorResponse(
                "CUST-NOTFOUND-001",
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}