package com.sgdc.core.security.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    @Value("${recaptcha.secret}")
    private String secret;
    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate rest = new RestTemplate();

    public boolean verify(String response) {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("secret", secret);
        params.add("response", response);
        ReCaptchaResponse api =
                rest.postForObject(VERIFY_URL, params, ReCaptchaResponse.class);
        log.info("Captcha API response: {}", api);
        return api != null && api.isSuccess();
    }

    private static class ReCaptchaResponse {
        @JsonProperty("success") private boolean success;
        // … otros campos si necesitas …
        public boolean isSuccess() { return success; }
    }
}

