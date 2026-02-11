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
import com.pacifico.customer.service.CustomerService;
import com.pacifico.customer.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Mono<CustomerResponse> createCustomer(HeaderRequest headerRequest, CustomerRequest customer) {

        log.info("Creando cliente {}", customer.documentNumber());

        return customerRepository
                .existsByDocumentTypeAndDocumentNumber(customer.documentType(), customer.documentNumber())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(
                                new BusinessException("El cliente con documento: " + customer.documentNumber() + " ya existe"));
                    }
                    CustomerEntity entity = customerMapper.toEntity(customer);
                    entity.setStatus(CustomerStatus.ACTIVE.name());
                    entity.setCreatedAt(LocalDateTime.now());
                    return customerRepository.save(entity);
                })
                .map(customerMapper::toResponse)
                .doOnSuccess(saved ->
                        log.info("Cliente creado correctamente")
                );
    }

    @Override
    public Mono<CustomerResponse> getCustomerByDocumentNumber(HeaderRequest headerRequest, String documentNumber) {

        log.info("Buscando cliente con documentNumber {}", documentNumber);
        return customerRepository.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Cliente no encontrado")
                ))
                .doOnSuccess(customer ->
                        log.info("Cliente encontrado con documentNumber {}", customer.getDocumentNumber())
                )
                .map(customerMapper::toResponse);
    }

    @Override
    public Flux<CustomerResponse> getAllCustomers(HeaderRequest headerRequest) {

        log.info("Obteniendo lista de clientes");

        return customerRepository.findAll()
                .map(customerMapper::toResponse);
    }

    @Override
    public Mono<Void> deactivateCustomer(HeaderRequest headerRequest, String documentNumber) {

        log.info("Desactivando cliente con documentNumber {}", documentNumber);

        return customerRepository.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Cliente no encontrado")
                ))
                .flatMap(customer -> {

                    if (Objects.equals(customer.getStatus(), CustomerStatus.INACTIVE.name())) {
                        return Mono.error(
                                new BusinessException("El cliente ya se encuentra inactivo")
                        );
                    }

                    customer.setStatus(CustomerStatus.INACTIVE.name());
                    return customerRepository.save(customer)
                            .doOnSuccess(saved ->
                                    log.info("Cliente {} desactivado correctamente", saved.getId())
                            );
                })
                .then();
    }
}
