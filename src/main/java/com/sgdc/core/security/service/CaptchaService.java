/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

