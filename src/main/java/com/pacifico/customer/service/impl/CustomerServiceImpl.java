package com.pacifico.customer.service.impl;

import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.exception.NotFoundException;
import com.pacifico.customer.mapper.CustomerMapper;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.entity.CustomerEntity;
import com.pacifico.customer.model.enums.CustomerStatus;
import com.pacifico.customer.repository.CustomerRepository;
import com.pacifico.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    public Mono<CustomerResponse> createCustomer(CustomerRequest customer) {

        log.info("Creando cliente {}", customer.documentNumber());

        return customerRepository
                .existsByDocumentTypeAndDocumentNumber(customer.documentType(), customer.documentNumber())
                .flatMap(exists -> {
                    if (exists)
                        return Mono.error(
                                new BusinessException("El cliente con documento: " + customer.documentNumber() + "ya existe"));

                    CustomerEntity entity = customerMapper.toEntity(customer);
                    entity.setId(UUID.randomUUID());
                    entity.setStatus(CustomerStatus.ACTIVE);
                    entity.setCreatedAt(LocalDateTime.now());
                    return customerRepository.save(entity);
                })
                .map(customerMapper::toResponse);
    }

    @Override
    public Mono<CustomerResponse> getCustomerById(UUID id) {

        log.info("Buscando cliente con id {}", id);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Cliente no encontrado")
                ))
                .map(customerMapper::toResponse);
    }

    @Override
    public Flux<CustomerResponse> getAllCustomers() {

        log.info("Obteniendo lista de clientes");

        return customerRepository.findAll()
                .map(customerMapper::toResponse);
    }

    @Override
    public Mono<Void> deactivateCustomer(UUID id) {

        log.info("Desactivando cliente con id {}", id);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Cliente no encontrado")
                ))
                .flatMap(customer -> {

                    if (customer.getStatus() == CustomerStatus.INACTIVE) {
                        return Mono.error(
                                new BusinessException("El cliente ya se encuentra inactivo")
                        );
                    }

                    customer.setStatus(CustomerStatus.INACTIVE);
                    return customerRepository.save(customer);
                })
                .then();
    }
}
