package com.sgdc.core.config.validator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BooleanStringValidator implements ConfigValidator {

    @Override
    public boolean supports(String key) {
        // Soporta cualquier parámetro booleano definido en el sistema.
        return List.of("notificaciones_activadas").contains(key);
    }

    @Override
    public void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío");
        }
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("El valor debe ser 'true' o 'false'");
        }
    }
}
