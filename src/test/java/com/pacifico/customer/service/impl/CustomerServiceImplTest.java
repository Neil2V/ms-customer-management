package com.pacifico.customer.service.impl;

import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.mapper.CustomerMapper;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.entity.CustomerEntity;
import com.pacifico.customer.model.enums.CustomerStatus;
import com.pacifico.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest validRequest;
    private CustomerEntity customerEntity;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        validRequest = buildCustomerRequest();
        customerEntity = buildCustomerEntity();
        customerResponse = buildCustomerResponse(customerEntity);
    }

    @Test
    void givenExistingCustomer_whenCreateCustomer_thenThrowsBusinessException() {

        // Given
        when(customerRepository.existsByDocumentTypeAndDocumentNumber(
                validRequest.documentType(),
                validRequest.documentNumber())
        ).thenReturn(Mono.just(true));

        // When
        Mono<CustomerResponse> result = customerService.createCustomer(validRequest);

        // Then
        StepVerifier.create(result)
                .expectError(BusinessException.class)
                .verify();

        verify(customerRepository, never()).save(any());
    }

    @Test
    void givenNewCustomer_whenCreateCustomer_thenCustomerIsCreated() {

        // Given
        when(customerRepository.existsByDocumentTypeAndDocumentNumber(
                validRequest.documentType(),
                validRequest.documentNumber())
        ).thenReturn(Mono.just(false));

        when(customerMapper.toEntity(validRequest)).thenReturn(customerEntity);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(Mono.just(customerEntity));
        when(customerMapper.toResponse(customerEntity)).thenReturn(customerResponse);

        // When
        Mono<CustomerResponse> result = customerService.createCustomer(validRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(customerResponse)
                .verifyComplete();

        verify(customerRepository).save(any(CustomerEntity.class));
    }

    // ---------- helpers ----------

    private CustomerRequest buildCustomerRequest() {
        return new CustomerRequest(
                "DNI",
                "12345678",
                "John Doe",
                "john@gmail.com"
        );
    }

    private CustomerEntity buildCustomerEntity() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setDocumentType("DNI");
        entity.setDocumentNumber("12345678");
        entity.setFullName("John Doe");
        entity.setEmail("john@gmail.com");
        entity.setStatus(CustomerStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    private CustomerResponse buildCustomerResponse(CustomerEntity entity) {
        return new CustomerResponse(
                entity.getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
