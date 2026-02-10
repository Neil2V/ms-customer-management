package com.pacifico.customer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.pacifico.customer.util.Constants.HEADERS;
import static com.pacifico.customer.util.Constants.MANDATORY_VALUE;

@Getter
@AllArgsConstructor
public enum BusinessErrorCodes {
    BUSINESS_ERROR_HEADER_TRANSACTION_ID_NULL(
            "001", "idTransaccion", "Transaccion-Id ".concat(MANDATORY_VALUE)),
    BUSINESS_ERROR_HEADER_APPLICATION_ID_NULL(
            "002", "idAplicacion", "Aplicacion-Id ".concat(MANDATORY_VALUE)),
    BUSINESS_ERROR_HEADER_APPLICATION_NAME_NULL(
            "003", "nombreAplicacion", "Nombre-Aplicacion ".concat(MANDATORY_VALUE)),
    BUSINESS_ERROR_HEADER_USER_CONSUMER_ID_NULL(
            "004", "idUsuarioConsumidor", "Usuario-Consumidor-Id ".concat(MANDATORY_VALUE)),
    BUSINESS_ERROR_HEADER_CONSUMER_SERVICE_NAME_NULL(
            "005", "nombreServicioConsumidor", "Nombre-Servicio-Consumidor ".concat(MANDATORY_VALUE)),
    BUSINESS_VALID_HEADERS_CHARACTERS_TYPE(
            "006", HEADERS, "No cumple el regex del header solo letras, numeros y guion"),
    BUSINESS_ERROR_STATE("007", "estado", "Estado ".concat(MANDATORY_VALUE)),
    BUSINESS_ERROR_FECH_CHANGE_STATE(
            "008", "fechaCambioEstado", "FechaCambioEstado ".concat(MANDATORY_VALUE)),

    MESSAGE_NOT_FOUND_LIST("301","ObjectBD","No se encontro ningun recurso."),
    MESSAGE_DATABASE_QUERY("302","ObjectBD","Error en la consulta BD.");

    private String code;
    private String title;
    private String description;
}