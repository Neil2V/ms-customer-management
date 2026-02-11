package com.pacifico.customer.service;

import com.pacifico.customer.controller.request.HeaderRequest;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerService {

    Mono<CustomerResponse> createCustomer(HeaderRequest headerRequest, CustomerRequest customer);
    Mono<CustomerResponse> getCustomerByDocumentNumber(HeaderRequest headerRequest, String documentNumber);
    Flux<CustomerResponse> getAllCustomers(HeaderRequest headerRequest);
    Mono<Void> deactivateCustomer(HeaderRequest headerRequest, String documentNumber);
}
