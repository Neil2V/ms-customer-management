package com.pacifico.customer.controller;

import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.exception.NotFoundException;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.enums.CustomerStatus;
import com.pacifico.customer.service.CustomerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerController.class)
class CustomerControllerTest {

    private final WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    @Autowired
    CustomerControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    // =========================
    // POST /customers
    // =========================
    @Nested
    class createCustomer {

        @Test
        void givenValidCustomerRequest_whenCreateCustomer_thenReturnsCustomerResponse() {

            // Arrange
            UUID generatedId = UUID.randomUUID();
            CustomerResponse response = activeCustomerResponse(generatedId);

            when(customerService.createCustomer(any()))
                    .thenReturn(Mono.just(response));

            // Act y Assert
            webTestClient.post()
                    .uri("/customers")
                    .bodyValue(validCustomerRequest())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CustomerResponse.class)
                    .value(r -> {
                        assertNotNull(r.id());
                        assertEquals("Juan Perez", r.fullName());
                        assertEquals(CustomerStatus.ACTIVE.name(), r.status());
                    });
        }

        // =========================
        // POST /customers (400)
        // =========================
        @Test
        void givenInvalidCustomerRequest_whenCreateCustomer_thenReturnsBadRequest() {

            // Arrange
            CustomerRequest invalidRequest = invalidCustomerRequest();

            // Act y Assert
            webTestClient.post()
                    .uri("/customers")
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    // =========================
    // GET /customers/{id}
    // =========================
    @Nested
    class getCustomerById {
        @Test
        void givenExistingCustomerId_whenGetCustomerById_thenReturnsCustomerResponse() {

            // Arrange
            UUID customerId = UUID.randomUUID();
            CustomerResponse response = activeCustomerResponse(customerId);

            when(customerService.getCustomerById(customerId))
                    .thenReturn(Mono.just(response));

            // Act y Assert
            webTestClient.get()
                    .uri("/customers/{id}", customerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CustomerResponse.class)
                    .value(r -> assertEquals(customerId, r.id()));
        }

        @Test
        void givenNonExistingCustomerId_whenGetCustomerById_thenReturnsNotFound() {

            // Arrange
            UUID id = UUID.randomUUID();

            when(customerService.getCustomerById(id))
                    .thenReturn(Mono.error(new NotFoundException("Cliente no encontrado")));

            // Act y Assert
            webTestClient.get()
                    .uri("/customers/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // =========================
    // GET /customers
    // =========================
    @Nested
    class getAllCustomers {

        @Test
        void givenCustomersExist_whenGetAllCustomers_thenReturnsCustomerList() {

            // Arrange
            CustomerResponse customer1 = activeCustomerResponse(UUID.randomUUID());
            CustomerResponse customer2 = activeCustomerResponse(UUID.randomUUID());

            when(customerService.getAllCustomers())
                    .thenReturn(Flux.just(customer1, customer2));

            // Act y Assert
            webTestClient.get()
                    .uri("/customers")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerResponse.class)
                    .hasSize(2)
                    .value(list -> {
                        assertNotNull(list.get(0).id());
                        assertNotNull(list.get(1).id());
                    });
        }

        @Test
        void givenNoCustomersExist_whenGetAllCustomers_thenReturnsEmptyList() {

            // Arrange
            when(customerService.getAllCustomers())
                    .thenReturn(Flux.empty());

            // Act y Assert
            webTestClient.get()
                    .uri("/customers")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerResponse.class)
                    .hasSize(0);
        }
    }


    // =========================
    // PATCH /customers/{id}/deactive
    // =========================
    @Nested
    class caseDeactivateCustomer {
        @Test
        void givenActiveCustomer_whenDeactivateCustomer_thenReturnsOk() {

            // Arrange
            UUID id = UUID.randomUUID();

            when(customerService.deactivateCustomer(id))
                    .thenReturn(Mono.empty());

            // Act y Assert
            webTestClient.patch()
                    .uri("/customers/{id}/deactive", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().isEmpty();
        }

        @Test
        void givenInactiveCustomer_whenDeactivateCustomer_thenReturnsConflict() {

            // Arrange
            UUID id = UUID.randomUUID();

            when(customerService.deactivateCustomer(id))
                    .thenReturn(
                            Mono.error(new BusinessException("El cliente ya se encuentra inactivo"))
                    );

            // Act y Assert
            webTestClient.patch()
                    .uri("/customers/{id}/deactive", id)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void givenNonExistingCustomerId_whenDeactivateCustomer_thenReturnsNotFound() {

            // Arrange
            UUID id = UUID.randomUUID();

            when(customerService.deactivateCustomer(id))
                    .thenReturn(
                            Mono.error(new NotFoundException("Cliente no encontrado"))
                    );

            // Act y Assert
            webTestClient.patch()
                    .uri("/customers/{id}/deactive", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    private CustomerRequest validCustomerRequest() {
        return new CustomerRequest(
                "dni",
                "789456145",
                "Juan Perez",
                "juan@gmail.com"
        );
    }

    private CustomerRequest invalidCustomerRequest() {
        return new CustomerRequest(
                "dni",
                "789456145",
                "Juan",
                "juan"  // invalid email
        );
    }

    private CustomerResponse activeCustomerResponse(UUID id) {
        return new CustomerResponse(
                id,
                "dni",
                "12345678",
                "Juan Perez",
                "juan@test.com",
                CustomerStatus.ACTIVE.name(),
                LocalDateTime.now()
        );
    }

    private CustomerResponse inactiveCustomerResponse(UUID id) {
        return new CustomerResponse(
                id,
                "dni",
                "12345678",
                "Juan Perez",
                "juan@test.com",
                CustomerStatus.INACTIVE.name(),
                LocalDateTime.now()
        );
    }
}