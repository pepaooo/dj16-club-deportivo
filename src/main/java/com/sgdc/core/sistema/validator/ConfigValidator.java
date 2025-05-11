package com.sgdc.core.sistema.validator;

public interface ConfigValidator {
    boolean supports(String key);
    void validate(String value);
}
