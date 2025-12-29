package com.pacifico.customer.model.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> details,
        LocalDateTime timestamp
) {}
