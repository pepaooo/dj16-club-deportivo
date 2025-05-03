package com.sgdc.core.security.filter;

import com.sgdc.core.security.exception.CaptchaException;
import com.sgdc.core.security.service.CaptchaService;
import com.sgdc.core.security.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CaptchaValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CaptchaValidationFilter.class);

    private final LoginAttemptService loginAttemptService;

    private final CaptchaService captchaService;

    public CaptchaValidationFilter(LoginAttemptService loginAttemptService,
                                   CaptchaService captchaService) {
        this.loginAttemptService = loginAttemptService;
        this.captchaService = captchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        if ("/login".equals(req.getRequestURI())
                && "POST".equalsIgnoreCase(req.getMethod())) {

            String username = req.getParameter("username");
            if (loginAttemptService.isCaptchaRequired(username)) {
                String token = req.getParameter("g-recaptcha-response");
                if (token == null || !captchaService.verify(token)) {
                    throw new CaptchaException("Captcha inválido");
                }
            }
        }
        chain.doFilter(req, res);
    }
}

