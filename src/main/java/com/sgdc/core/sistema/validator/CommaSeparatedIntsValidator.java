package com.sgdc.core.sistema.validator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class CommaSeparatedIntsValidator implements ConfigValidator {

    @Override
    public boolean supports(String key) {
        // Soporta cualquier parámetro que sea una lista de enteros separados por comas.
        return List.of("notificacion_periodo").contains(key);
    }

    private static final Pattern PATTERN =
            Pattern.compile("^\\d+(,\\d+)*$");

    @Override
    public void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El valor debe ser una lista de enteros separados por comas");
        }
    }
}
