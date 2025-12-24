package com.pacifico.customer.model.entity;

import com.pacifico.customer.model.enums.CustomerStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("customers")
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
    private CustomerStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;
}
