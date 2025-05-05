package com.sgdc.core.config.validator;

public interface ConfigValidator {
    boolean supports(String key);
    void validate(String value);
}
