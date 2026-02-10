package com.pacifico.customer.validation;

import com.pacifico.customer.util.Messages;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    public void validatePatter(String nameField, String value, List<String> errors) {
        if (Objects.nonNull(value)) {
            String error;
            Pattern patron = Pattern.compile(RegularExpression.NOT_SPECIAL_CHARACTERS);
            Matcher matcher = patron.matcher(StringUtils.trim(value));
            if (!matcher.matches()) {
                error = Messages.HEADER_ONLY_LETTERS_NUMBER_HYPHEN;
                errors.add(error);
            }
        }
    }
}
