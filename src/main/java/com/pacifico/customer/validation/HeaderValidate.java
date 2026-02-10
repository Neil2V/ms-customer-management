package com.pacifico.customer.validation;

import com.pacifico.customer.controller.request.HeaderRequest;
import com.pacifico.customer.exception.BusinessErrorCodes;
import com.pacifico.customer.exception.BusinessException;
import com.pacifico.customer.model.dto.CustomerRequest;
import com.pacifico.customer.util.Constants;
import com.pacifico.customer.util.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class HeaderValidate extends Validate{

    public Mono<Void> validateGetCustomer(HeaderRequest header) {
        final long timeStart = Logger.startMethodLogger(header.getTransactionId(), header.getApplicationId());
        Logger.paramsInputLogger(header.getTransactionId(), header.getApplicationId(), header);

        if (Strings.isNullOrEmpty(header.getTransactionId())) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_ERROR_HEADER_TRANSACTION_ID_NULL));
        }

        if (Strings.isNullOrEmpty(header.getApplicationId())) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_ERROR_HEADER_APPLICATION_ID_NULL));
        }

        if (Strings.isNullOrEmpty(header.getApplicationName())) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_ERROR_HEADER_APPLICATION_NAME_NULL));
        }

        if (Strings.isNullOrEmpty(header.getUserConsumerId())) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_ERROR_HEADER_USER_CONSUMER_ID_NULL));
        }

        if (Strings.isNullOrEmpty(header.getConsumerServiceName())) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_ERROR_HEADER_CONSUMER_SERVICE_NAME_NULL));
        }

        List<String> characterErrors = new ArrayList<>();
        onlyNumberAndLetters(header, characterErrors);
        if (!characterErrors.isEmpty()) {
            return Mono.error(BusinessException.createException(BAD_REQUEST,
                    BusinessErrorCodes.BUSINESS_VALID_HEADERS_CHARACTERS_TYPE));
        }

        Logger.finishMethodLogger(header.getTransactionId(), header.getApplicationId(), timeStart);
        return Mono.empty().then();
    }

    public void onlyNumberAndLetters(HeaderRequest header, List<String> characterErrors) {
        validatePatter(Constants.TRANSACTION_ID, header.getTransactionId(), characterErrors);
        validatePatter(Constants.APPLICATION_NAME, header.getApplicationName(), characterErrors);
        validatePatter(Constants.APPLICATION_ID, header.getApplicationId(), characterErrors);
        validatePatter(Constants.USER_CONSUMER_ID, header.getUserConsumerId(), characterErrors);
        validatePatter(Constants.CONSUMER_SERVICE_NAME, header.getConsumerServiceName(), characterErrors);
    }
}
