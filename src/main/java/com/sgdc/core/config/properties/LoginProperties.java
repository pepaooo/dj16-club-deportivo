package com.sgdc.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 1. Clase de propiedades
@Configuration
@ConfigurationProperties(prefix = "login.attempts")
@Data
public class LoginProperties {
    private int maxAttempts    = 5;
    private int captchaThreshold = 3;
    private long lockDurationMin = 5;
}

