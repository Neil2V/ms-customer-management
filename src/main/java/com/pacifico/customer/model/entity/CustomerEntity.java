package com.pacifico.customer.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(schema = "schema_customer", name = "customers")
@Data
public class CustomerEntity {

    @Id
    private UUID id;

    @Column("document_type")
    private String documentType;

    @Column("document_number")
    private String documentNumber;

    @Column("full_name")
    private String fullName;

    @Column("email")
    private String email;

    @Column("status")
    private String  status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("columna_creada")
    private LocalDateTime columnaCreada;
}
