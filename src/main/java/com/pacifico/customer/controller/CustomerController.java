package com.pacifico.customer.controller;

import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Mono<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public Mono<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public Flux<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PatchMapping("{id}/deactive")
    public Mono<Void> updateCustomerStatus(@PathVariable UUID id) {
        return customerService.deactivateCustomer(id);
    }
}
