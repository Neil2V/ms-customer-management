package com.pacifico.customer.mapper;

import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.model.dto.CustomerResponse;
import com.pacifico.customer.model.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity toEntity(CustomerRequest request);

    CustomerResponse toResponse(CustomerEntity entity);
}
