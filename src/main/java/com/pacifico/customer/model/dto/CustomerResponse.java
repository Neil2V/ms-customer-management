package com.pacifico.customer.model.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        String status,
        LocalDateTime createdAt
) {}