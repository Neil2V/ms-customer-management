package com.pacifico.customer.validation;

public final class RegularExpression {
    private RegularExpression() {}
    // Solo letras, números y guion
    public static final String NOT_SPECIAL_CHARACTERS = "^[a-zA-Z0-9.-]+$";
}

