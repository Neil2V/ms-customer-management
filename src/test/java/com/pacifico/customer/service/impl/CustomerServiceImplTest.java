package com.pacifico.customer.service.impl;

import com.pacifico.customer.controller.request.HeaderRequest;
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
    private HeaderRequest headerRequest;

    @BeforeEach
    void setUp() {
        validRequest = buildCustomerRequest();
        customerEntity = buildCustomerEntity();
        customerResponse = buildCustomerResponse(customerEntity);
        headerRequest = HeaderRequest.builder()
                .transactionId("transactionID")
                .applicationId("applicationID")
                .applicationName("applicationName")
                .userConsumerId("userConsumerID")
                .consumerServiceName("consumerServiceName")
                .build();
    }

    @Nested
    class createCustomer {
        @Test
        void givenExistingCustomer_whenCreateCustomer_thenThrowsBusinessException() {

            when(customerRepository.existsByDocumentTypeAndDocumentNumber(
                    validRequest.documentType(),
                    validRequest.documentNumber())
            ).thenReturn(Mono.just(true));

            Mono<CustomerResponse> result = customerService.createCustomer(headerRequest, validRequest);

            StepVerifier.create(result)
                    .expectError(BusinessException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void givenNewCustomer_whenCreateCustomer_thenCustomerIsCreated() {

            when(customerRepository.existsByDocumentTypeAndDocumentNumber(
                    validRequest.documentType(),
                    validRequest.documentNumber())
            ).thenReturn(Mono.just(false));

            when(customerMapper.toEntity(validRequest)).thenReturn(customerEntity);
            when(customerRepository.save(any(CustomerEntity.class))).thenReturn(Mono.just(customerEntity));
            when(customerMapper.toResponse(customerEntity)).thenReturn(customerResponse);

            Mono<CustomerResponse> result = customerService.createCustomer(headerRequest, validRequest);

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

            CustomerEntity entity1 = buildCustomerEntity();
            CustomerEntity entity2 = buildCustomerEntity();

            CustomerResponse response1 = buildCustomerResponse(entity1);
            CustomerResponse response2 = buildCustomerResponse(entity2);

            when(customerRepository.findAll())
                    .thenReturn(Flux.just(entity1, entity2));

            when(customerMapper.toResponse(entity1)).thenReturn(response1);
            when(customerMapper.toResponse(entity2)).thenReturn(response2);

            Flux<CustomerResponse> result = customerService.getAllCustomers(headerRequest);

            StepVerifier.create(result)
                    .expectNext(response1)
                    .expectNext(response2)
                    .verifyComplete();
        }

        @Test
        void givenNoCustomers_whenGetAllCustomers_thenReturnEmptyFlux() {

            when(customerRepository.findAll())
                    .thenReturn(Flux.empty());

            Flux<CustomerResponse> result = customerService.getAllCustomers(headerRequest);

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    class getCustomerById {

        @Test
        void givenExistingCustomer_whenGetCustomerById_thenReturnCustomer() {

            CustomerEntity entity = buildCustomerEntity();
            CustomerResponse response = buildCustomerResponse(entity);

            when(customerRepository.findByDocumentNumber(anyString()))
                    .thenReturn(Mono.just(entity));

            when(customerMapper.toResponse(entity))
                    .thenReturn(response);

            Mono<CustomerResponse> result = customerService.getCustomerByDocumentNumber(headerRequest, entity.getDocumentNumber());

            StepVerifier.create(result)
                    .expectNext(response)
                    .verifyComplete();
        }

        @Test
        void givenCustomerNotFound_whenGetCustomerById_thenThrowsNotFoundException() {

            String documentNumber = "654874";

            when(customerRepository.findByDocumentNumber(anyString()))
                    .thenReturn(Mono.empty());

            Mono<CustomerResponse> result = customerService.getCustomerByDocumentNumber(headerRequest, documentNumber);

            StepVerifier.create(result)
                    .expectError(NotFoundException.class)
                    .verify();
        }
    }

    @Nested
    class deactivateCustomer {
        @Test
        void givenNonExistingCustomer_whenDeactivateCustomer_thenThrowsNotFoundException() {

            String documentNumber = "654874";
            when(customerRepository.findByDocumentNumber(anyString()))
                    .thenReturn(Mono.empty());

            Mono<Void> result = customerService.deactivateCustomer(headerRequest, documentNumber);

            StepVerifier.create(result)
                    .expectError(NotFoundException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void givenInactiveCustomer_whenDeactivateCustomer_thenThrowsBusinessException() {

            String documentNumber = "654874";

            CustomerEntity customer = new CustomerEntity();
            customer.setStatus(CustomerStatus.INACTIVE.name());

            when(customerRepository.findByDocumentNumber(anyString()))
                    .thenReturn(Mono.just(customer));

            Mono<Void> result = customerService.deactivateCustomer(headerRequest, documentNumber);

            StepVerifier.create(result)
                    .expectError(BusinessException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void givenActiveCustomer_whenDeactivateCustomer_thenCustomerIsDeactivated() {

            String documentNumber = "654874";

            CustomerEntity customer = new CustomerEntity();
            customer.setStatus(CustomerStatus.ACTIVE.name());

            when(customerRepository.findByDocumentNumber(anyString()))
                    .thenReturn(Mono.just(customer));

            when(customerRepository.save(any(CustomerEntity.class)))
                    .thenReturn(Mono.just(customer));

            Mono<Void> result = customerService.deactivateCustomer(headerRequest, documentNumber);

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
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
