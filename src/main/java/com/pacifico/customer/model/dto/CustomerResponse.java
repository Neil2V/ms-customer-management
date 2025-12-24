package com.pacifico.customer.model.dto;

import com.pacifico.customer.model.enums.CustomerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerResponse {
    private UUID id;
    private String documentType;
    private String documentNumber;
    private String fullName;
    private String email;
    private CustomerStatus status;
    private LocalDateTime createdAt;
}
