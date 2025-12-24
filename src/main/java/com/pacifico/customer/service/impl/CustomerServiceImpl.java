package com.pacifico.customer.service.impl;

import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.repository.CustomerRepository;
import com.pacifico.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Mono<CustomerResponse> createCustomer(CustomerRequest customer) {
        return Mono.empty();
    }

    @Override
    public Mono<CustomerResponse> getCustomerById(UUID id) {
        return Mono.empty();
    }

    @Override
    public Flux<CustomerResponse> getAllCustomers() {
        return Flux.empty();
    }

    @Override
    public Mono<Void> deactivateCustomer(UUID id) {
        return Mono.empty();
    }
}
