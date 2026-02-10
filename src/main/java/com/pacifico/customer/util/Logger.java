package com.pacifico.customer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

@Slf4j
@UtilityClass
public final class Logger {

    private static String sanitize(String value) {
        return value != null ? StringEscapeUtils.escapeJava(value) : "null";
    }

    public static long startMethodLogger(String idTransaction, String idCorrelative) {
        String nameMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        long timeStart = System.currentTimeMillis();

        log.info(Messages.LOG_START, sanitize(nameClass), lineNumber, sanitize(idTransaction), sanitize(idCorrelative), sanitize(nameMethod));
        return timeStart;
    }

    /**
     * Método que se encarga de generar el log al finalizar un método.
     *
     * @param idTransaction campos de auditoria obtenidos desde el header.
     * @param idCorrelative campos de auditoria obtenidos desde el header.
     * @param timeStart     tiempo de inicio del método, expresado en milisegundos.
     */
    public static void finishMethodLogger(String idTransaction, String idCorrelative, long timeStart) {
        String nameMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        long timeDuration = System.currentTimeMillis() - timeStart;

        log.info(Messages.LOG_FINISH, sanitize(nameClass),
                lineNumber, sanitize(idTransaction), sanitize(idCorrelative), sanitize(nameMethod), timeDuration);
    }

    /**
     * Método que se encarga de generar el log mostrando los parámetros de entrada.
     *
     * @param idTransaction campos de auditoria obtenidos desde el header.
     * @param idCorrelative campos de auditoria obtenidos desde el header.
     * @param input         parámetros de entrada.
     */
    public static void paramsInputLogger(String idTransaction, String idCorrelative, Object input) {
        String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

        log.info(Messages.LOG_PARAMS_IN, sanitize(nameClass),
                lineNumber, sanitize(idTransaction), sanitize(idCorrelative), sanitize(input != null ? input.toString() : "null"));
    }

    /**
     * Método que se encarga de generar el log mostrando los parámetros de salida.
     *
     * @param idTransaction campos de auditoria obtenidos desde el header.
     * @param idCorrelative campos de auditoria obtenidos desde el header.
     * @param output        parámetros de salida.
     */
    public static void paramsOutputLogger(String idTransaction, String idCorrelative, Object output) {
        String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

        String jsonOutput = convertToJson(output);

        log.info(Messages.LOG_PARAMS_OUT, sanitize(nameClass),
                lineNumber, sanitize(idTransaction), sanitize(idCorrelative), sanitize(jsonOutput));
    }

    public static String convertToJson(Object object) {
        if (object == null) {
            return "null";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON", e);
            return "Error converting object to JSON";
        }
    }

}