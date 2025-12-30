package com.pacifico.customer.service.impl;

import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.exception.NotFoundException;
import com.pacifico.customer.mapper.CustomerMapper;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.entity.CustomerEntity;
import com.pacifico.customer.model.enums.CustomerStatus;
import com.pacifico.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Nested
    class createCustomer {
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
    }

    @Nested
    class listAllCustomers {
        @Test
        void givenCustomersExist_whenGetAllCustomers_thenReturnList() {

            // Given
            CustomerEntity entity1 = buildCustomerEntity();
            CustomerEntity entity2 = buildCustomerEntity();

            CustomerResponse response1 = buildCustomerResponse(entity1);
            CustomerResponse response2 = buildCustomerResponse(entity2);

            when(customerRepository.findAll())
                    .thenReturn(Flux.just(entity1, entity2));

            when(customerMapper.toResponse(entity1)).thenReturn(response1);
            when(customerMapper.toResponse(entity2)).thenReturn(response2);

            // When
            Flux<CustomerResponse> result = customerService.getAllCustomers();

            // Then
            StepVerifier.create(result)
                    .expectNext(response1)
                    .expectNext(response2)
                    .verifyComplete();
        }

        @Test
        void givenNoCustomers_whenGetAllCustomers_thenReturnEmptyFlux() {

            // Given
            when(customerRepository.findAll())
                    .thenReturn(Flux.empty());

            // When
            Flux<CustomerResponse> result = customerService.getAllCustomers();

            // Then
            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    class getCustomerById {

        @Test
        void givenExistingCustomer_whenGetCustomerById_thenReturnCustomer() {

            // Given
            UUID id = UUID.randomUUID();
            CustomerEntity entity = buildCustomerEntity();
            CustomerResponse response = buildCustomerResponse(entity);

            when(customerRepository.findById(id))
                    .thenReturn(Mono.just(entity));

            when(customerMapper.toResponse(entity))
                    .thenReturn(response);

            // When
            Mono<CustomerResponse> result = customerService.getCustomerById(id);

            // Then
            StepVerifier.create(result)
                    .expectNext(response)
                    .verifyComplete();
        }

        @Test
        void givenCustomerNotFound_whenGetCustomerById_thenThrowsNotFoundException() {

            // Given
            UUID id = UUID.randomUUID();

            when(customerRepository.findById(id))
                    .thenReturn(Mono.empty());

            // When
            Mono<CustomerResponse> result = customerService.getCustomerById(id);

            // Then
            StepVerifier.create(result)
                    .expectError(NotFoundException.class)
                    .verify();
        }
    }

    @Nested
    class deactivateCustomer {
        @Test
        void givenNonExistingCustomer_whenDeactivateCustomer_thenThrowsNotFoundException() {

            // Arrange
            UUID id = UUID.randomUUID();
            when(customerRepository.findById(id))
                    .thenReturn(Mono.empty());

            // Act
            Mono<Void> result = customerService.deactivateCustomer(id);

            // Assert
            StepVerifier.create(result)
                    .expectError(NotFoundException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void givenInactiveCustomer_whenDeactivateCustomer_thenThrowsBusinessException() {

            // Arrange
            UUID id = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(id);
            customer.setStatus(CustomerStatus.INACTIVE.name());

            when(customerRepository.findById(id))
                    .thenReturn(Mono.just(customer));

            // Act
            Mono<Void> result = customerService.deactivateCustomer(id);

            // Assert
            StepVerifier.create(result)
                    .expectError(BusinessException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void givenActiveCustomer_whenDeactivateCustomer_thenCustomerIsDeactivated() {

            // Arrange
            UUID id = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(id);
            customer.setStatus(CustomerStatus.ACTIVE.name());

            when(customerRepository.findById(id))
                    .thenReturn(Mono.just(customer));

            when(customerRepository.save(any(CustomerEntity.class)))
                    .thenReturn(Mono.just(customer));

            // Act
            Mono<Void> result = customerService.deactivateCustomer(id);

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();

            verify(customerRepository).save(customer);
            assertEquals(CustomerStatus.INACTIVE.name(), customer.getStatus());
        }
    }

    // ---------- helpers ----------

    private CustomerRequest buildCustomerRequest() {
        return new CustomerRequest(
                "DNI",
                "12345678",
                "Neil Vara",
                "neil@gmail.com"
        );
    }

    private CustomerEntity buildCustomerEntity() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setDocumentType("DNI");
        entity.setDocumentNumber("12345678");
        entity.setFullName("Neil Vara");
        entity.setEmail("neil@gmail.com");
        entity.setStatus(CustomerStatus.ACTIVE.name());
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
