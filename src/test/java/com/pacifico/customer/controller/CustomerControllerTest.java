package com.pacifico.customer.controller;

import com.pacifico.customer.controller.request.HeaderRequest;
import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.exception.NotFoundException;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.enums.CustomerStatus;
import com.pacifico.customer.service.CustomerService;
import com.pacifico.customer.validation.HeaderValidate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerController.class)
class CustomerControllerTest {

    private final WebTestClient webTestClient;

    @MockBean
    private HeaderValidate headerValidate;

    @MockBean
    private CustomerService customerService;

    private static HttpHeaders httpHeaders;

    @BeforeAll
    static void initHeaders() {
        HeaderRequest headerRequest = HeaderRequest.builder()
                .transactionId("transactionID")
                .applicationId("applicationID")
                .applicationName("applicationName")
                .userConsumerId("userConsumerID")
                .consumerServiceName("consumerServiceName")
                .build();
        httpHeaders = buildHeaders(headerRequest);
    }

    private static HttpHeaders buildHeaders(HeaderRequest headerRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Transaction-Id", headerRequest.getTransactionId());
        headers.add("Aplicacion-Id", headerRequest.getApplicationId());
        headers.add("Aplicacion-Name", headerRequest.getApplicationName());
        headers.add("User-Consumer-Id", headerRequest.getUserConsumerId());
        headers.add("Consumer-Service-Name", headerRequest.getConsumerServiceName());
        return headers;
    }

    @Autowired
    CustomerControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void setupMocks() {
        when(headerValidate.validateGetCustomer(any())).thenReturn(Mono.empty());
    }

    @Nested
    class createCustomer {

        @Test
        void givenValidCustomerRequest_whenCreateCustomer_thenReturnsCustomerResponse() {

            CustomerResponse response = activeCustomerResponse("789456145");

            when(customerService.createCustomer(any()))
                    .thenReturn(Mono.just(response));

            webTestClient.post()
                    .uri("/customers")
                    .headers(headers -> headers.addAll(httpHeaders))
                    .bodyValue(validCustomerRequest())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CustomerResponse.class)
                    .value(r -> {
                        assertEquals("789456145", r.documentNumber());
                        assertEquals("Juan Perez", r.fullName());
                        assertEquals(CustomerStatus.ACTIVE.name(), r.status());
                    });
        }

        @Test
        void givenInvalidCustomerRequest_whenCreateCustomer_thenReturnsBadRequest() {

            CustomerRequest invalidRequest = invalidCustomerRequest();

            webTestClient.post()
                    .uri("/customers")
                    .headers(headers -> headers.addAll(httpHeaders))
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class getCustomerById {
        @Test
        void givenExistingCustomerId_whenGetCustomerById_thenReturnsCustomerResponse() {

            CustomerResponse response = activeCustomerResponse("789456145");

            when(customerService.getCustomerByDocumentNumber(anyString()))
                    .thenReturn(Mono.just(response));

            webTestClient.get()
                    .uri("/customers/{documentNumber}", "789456145")
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CustomerResponse.class)
                    .value(r -> assertEquals("789456145", response.documentNumber()));
        }

        @Test
        void givenNonExistingCustomerId_whenGetCustomerById_thenReturnsNotFound() {

            String documentNumber = "789456145";

            when(customerService.getCustomerByDocumentNumber(anyString()))
                    .thenReturn(Mono.error(new NotFoundException("Cliente no encontrado")));

            webTestClient.get()
                    .uri("/customers/{documentNumber}", documentNumber)
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class getAllCustomers {

        @Test
        void givenCustomersExist_whenGetAllCustomers_thenReturnsCustomerList() {

            CustomerResponse customer1 = activeCustomerResponse("789456145");
            CustomerResponse customer2 = activeCustomerResponse("123456789");

            when(customerService.getAllCustomers())
                    .thenReturn(Flux.just(customer1, customer2));

            webTestClient.get()
                    .uri("/customers")
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerResponse.class)
                    .hasSize(2)
                    .value(list -> {
                        assertEquals("789456145", list.get(0).documentNumber());
                        assertEquals("123456789", list.get(1).documentNumber());
                    });
        }

        @Test
        void givenNoCustomersExist_whenGetAllCustomers_thenReturnsEmptyList() {

            when(customerService.getAllCustomers())
                    .thenReturn(Flux.empty());

            webTestClient.get()
                    .uri("/customers")
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerResponse.class)
                    .hasSize(0);
        }
    }

    @Nested
    class caseDeactivateCustomer {
        @Test
        void givenActiveCustomer_whenDeactivateCustomer_thenReturnsOk() {

            String documentNumber = "74913215";

            when(customerService.deactivateCustomer(documentNumber))
                    .thenReturn(Mono.empty());

            webTestClient.patch()
                    .uri("/customers/{id}/deactive", documentNumber)
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().isEmpty();
        }

        @Test
        void givenInactiveCustomer_whenDeactivateCustomer_thenReturnsConflict() {

            String documentNumber = "74913215";

            when(customerService.deactivateCustomer(documentNumber))
                    .thenReturn(
                            Mono.error(new BusinessException("El cliente ya se encuentra inactivo"))
                    );

            webTestClient.patch()
                    .uri("/customers/{id}/deactive", documentNumber)
                    .headers(headers -> headers.addAll(httpHeaders))
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void givenNonExistingCustomerId_whenDeactivateCustomer_thenReturnsNotFound() {

            String documentNumber = "74913215";

            when(customerService.deactivateCustomer(documentNumber))
                    .thenReturn(
                            Mono.error(new NotFoundException("Cliente no encontrado"))
                    );

            webTestClient.patch()
                    .uri("/customers/{id}/deactive", documentNumber)
                    .headers(headers -> headers.addAll(httpHeaders))
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

    private CustomerResponse activeCustomerResponse(String documentNumber) {
        return new CustomerResponse(
                "dni",
                documentNumber,
                "Juan Perez",
                "juan@test.com",
                CustomerStatus.ACTIVE.name(),
                LocalDateTime.now()
        );
    }
}