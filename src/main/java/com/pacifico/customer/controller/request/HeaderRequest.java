package com.pacifico.customer.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class HeaderRequest implements Serializable {

    private static final long serialVersionUID = -987425516101290528L;

    @JsonProperty("idTransaccion")
    private String transactionId;
    @JsonProperty("idaplicacion")
    private String applicationId;
    @JsonProperty("nombreAplicacion")
    private String applicationName;
    @JsonProperty("idUsuarioConsumidor")
    private String userConsumerId;
    @JsonProperty("nombreServicioConsumidor")
    private String consumerServiceName;
    @JsonProperty("Ocp-Apim-Subscription-Key")
    private String subscriptionKey;
}