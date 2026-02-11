package com.pacifico.customer.repository;

import com.pacifico.customer.model.entity.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository
        extends ReactiveCrudRepository<CustomerEntity, UUID> {

    Mono<Boolean> existsByDocumentTypeAndDocumentNumber(
            String documentType,
            String documentNumber
    );

    Mono<CustomerEntity> findByDocumentNumber(String documentNumber);
}
