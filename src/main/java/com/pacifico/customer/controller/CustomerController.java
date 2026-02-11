package com.pacifico.customer.controller;

import com.pacifico.customer.controller.request.HeaderRequest;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.service.CustomerService;
import com.pacifico.customer.util.Constants;
import com.pacifico.customer.validation.HeaderValidate;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final HeaderValidate headerValidate;
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService, HeaderValidate headerValidate) {
        this.customerService = customerService;
        this.headerValidate = headerValidate;
    }

    @PostMapping
    public Mono<CustomerResponse> createCustomer(
            @RequestHeader(value = Constants.TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(value = Constants.APPLICATION_ID, required = false) String applicationId,
            @RequestHeader(value = Constants.APPLICATION_NAME, required = false) String applicationName,
            @RequestHeader(value = Constants.USER_CONSUMER_ID, required = false) String userConsumerId,
            @RequestHeader(value = Constants.CONSUMER_SERVICE_NAME, required = false) String consumerServiceName,
            @Valid @RequestBody CustomerRequest request) {
        final HeaderRequest header = HeaderRequest.builder()
                .transactionId(transactionId)
                .applicationId(applicationId)
                .applicationName(applicationName)
                .userConsumerId(userConsumerId)
                .consumerServiceName(consumerServiceName)
                .build();
        return headerValidate.validateGetCustomer(header)
                .then(customerService.createCustomer(header, request));
    }

    @GetMapping("/{documentNumber}")
    public Mono<CustomerResponse> getCustomerByDocumentNumber(
            @RequestHeader(value = Constants.TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(value = Constants.APPLICATION_ID, required = false) String applicationId,
            @RequestHeader(value = Constants.APPLICATION_NAME, required = false) String applicationName,
            @RequestHeader(value = Constants.USER_CONSUMER_ID, required = false) String userConsumerId,
            @RequestHeader(value = Constants.CONSUMER_SERVICE_NAME, required = false) String consumerServiceName,
            @PathVariable String documentNumber) {
        final HeaderRequest header = HeaderRequest.builder()
                .transactionId(transactionId)
                .applicationId(applicationId)
                .applicationName(applicationName)
                .userConsumerId(userConsumerId)
                .consumerServiceName(consumerServiceName)
                .build();
        return headerValidate.validateGetCustomer(header)
                .then(customerService.getCustomerByDocumentNumber(header, documentNumber));
    }

    @GetMapping
    public Flux<CustomerResponse> getAllCustomers(
            @RequestHeader(value = Constants.TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(value = Constants.APPLICATION_ID, required = false) String applicationId,
            @RequestHeader(value = Constants.APPLICATION_NAME, required = false) String applicationName,
            @RequestHeader(value = Constants.USER_CONSUMER_ID, required = false) String userConsumerId,
            @RequestHeader(value = Constants.CONSUMER_SERVICE_NAME, required = false) String consumerServiceName
    ) {
        final HeaderRequest header = HeaderRequest.builder()
                .transactionId(transactionId)
                .applicationId(applicationId)
                .applicationName(applicationName)
                .userConsumerId(userConsumerId)
                .consumerServiceName(consumerServiceName)
                .build();
        return headerValidate.validateGetCustomer(header)
                .thenMany(customerService.getAllCustomers(header));
    }

    @PatchMapping("{documentNumber}/deactive")
    public Mono<Void> updateCustomerStatus(
            @RequestHeader(value = Constants.TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(value = Constants.APPLICATION_ID, required = false) String applicationId,
            @RequestHeader(value = Constants.APPLICATION_NAME, required = false) String applicationName,
            @RequestHeader(value = Constants.USER_CONSUMER_ID, required = false) String userConsumerId,
            @RequestHeader(value = Constants.CONSUMER_SERVICE_NAME, required = false) String consumerServiceName,
            @PathVariable String documentNumber) {
        final HeaderRequest header = HeaderRequest.builder()
                .transactionId(transactionId)
                .applicationId(applicationId)
                .applicationName(applicationName)
                .userConsumerId(userConsumerId)
                .consumerServiceName(consumerServiceName)
                .build();
        return headerValidate.validateGetCustomer(header)
                 .then(customerService.deactivateCustomer(header, documentNumber));
    }
}
