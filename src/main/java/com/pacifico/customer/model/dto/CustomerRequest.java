package com.pacifico.customer.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(

        @NotBlank(message = "documentType es obligatorio")
        String documentType,

        @NotBlank(message = "documentNumber es obligatorio")
        @Size(min = 6, max = 20)
        String documentNumber,

        @NotBlank(message = "fullName es obligatorio")
        String fullName,

        @Email(message = "email inválido")
        String email
) {}
