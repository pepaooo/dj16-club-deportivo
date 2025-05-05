package com.sgdc.core.config.validator;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigValidationService {

    private final List<ConfigValidator> validators;

    public ConfigValidationService(List<ConfigValidator> validators) {
        this.validators = validators;
    }

    public void validate(String key, String raw) {
        // Busca el primer validador que soporte la clave
        ConfigValidator v = validators.stream()
                .filter(val -> val.supports(key))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("No hay validador para '" + key + "'"));
        v.validate(raw);
    }
}

