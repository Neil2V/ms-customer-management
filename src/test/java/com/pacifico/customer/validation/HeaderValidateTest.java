package com.pacifico.customer.validation;

import com.pacifico.customer.controller.request.HeaderRequest;
import com.pacifico.customer.exception.BusinessErrorCodes;
import com.pacifico.customer.exception.BusinessException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class HeaderValidateTest {

    private final HeaderValidate headerValidate = new HeaderValidate();

    @Test
    void validateGetCustomer_validHeaders_returnsEmptyMono() {
        HeaderRequest header = HeaderRequest.builder()
                .transactionId("TX123")
                .applicationId("APP123")
                .applicationName("APPNAME")
                .userConsumerId("USER123")
                .consumerServiceName("SERVICE123")
                .build();

        Mono<Void> result = headerValidate.validateGetCustomer(header);
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validateGetCustomer_missingTransactionId_returnsError() {
        HeaderRequest header = HeaderRequest.builder()
                .transactionId("")
                .applicationId("APP123")
                .applicationName("APPNAME")
                .userConsumerId("USER123")
                .consumerServiceName("SERVICE123")
                .build();

        Mono<Void> result = headerValidate.validateGetCustomer(header);
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getErrors() != null &&
                        ((BusinessException) e).getErrors().contains(BusinessErrorCodes.BUSINESS_ERROR_HEADER_TRANSACTION_ID_NULL.getTitle()))
                .verify();
    }

    @Test
    void validateGetCustomer_invalidCharacters_returnsError() {
        HeaderRequest header = HeaderRequest.builder()
                .transactionId("TX123$")
                .applicationId("APP123")
                .applicationName("APPNAME")
                .userConsumerId("USER123")
                .consumerServiceName("SERVICE123")
                .build();

        Mono<Void> result = headerValidate.validateGetCustomer(header);
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getErrors() != null &&
                        ((BusinessException) e).getErrors().contains(BusinessErrorCodes.BUSINESS_VALID_HEADERS_CHARACTERS_TYPE.getTitle()))
                .verify();
    }
}
