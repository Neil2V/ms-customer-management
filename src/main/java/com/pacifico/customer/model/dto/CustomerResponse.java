package com.pacifico.customer.model.dto;

import com.pacifico.customer.model.enums.CustomerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        CustomerStatus status,
        LocalDateTime createdAt
) {}