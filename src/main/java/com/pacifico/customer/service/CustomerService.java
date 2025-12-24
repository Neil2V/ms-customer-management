package com.pacifico.customer.service;

import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerService {

    Mono<CustomerResponse> createCustomer(CustomerRequest customer);
    Mono<CustomerResponse> getCustomerById(UUID id);
    Flux<CustomerResponse> getAllCustomers();
    Mono<Void> deactivateCustomer(UUID id);
}
