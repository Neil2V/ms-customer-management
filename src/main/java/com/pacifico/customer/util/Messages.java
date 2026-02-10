package com.pacifico.customer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    public static final String LOG_PARAMS_OUT = "{} : {} - [idTx={} cltId={}][Datos de salida: {}]";
    public static final String LOG_PARAMS_IN = "{} : {} - [idTx={} cltId={}][Datos de entrada: {}]";
    public static final String LOG_FINISH = "{} : {} - [idTx={} cltId={}][FIN {}]Tiempo total del proceso de ejecución: {} ms";
    public static final String LOG_START = "{} : {} - [idTx={} cltId={}][INICIO {}]";
    public static final String LOG_ERROR = "{} : {} - [idTx={} cltId={}][Error generado: {}]";

    public static final String HEADER_ONLY_LETTERS_NUMBER_HYPHEN = "No cumple el regex del header solo letras, numeros y guion";

    public static final String CODE_SUCCESS = "00";
    public static final String MESSAGE_SUCCESS = "Se actualizó el estado con éxito";
    public static final String CODE_ERROR = "FNL-FE-000";
    public static final String MESSAGE_ERROR = "No se encontró datos";
    public static final String NOT_PATTERN = "^[0-9]+$";
}