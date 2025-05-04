package com.sgdc.core.security.handler;

import com.sgdc.core.security.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler
        extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthSuccessHandler.class);

    private final LoginAttemptService loginAttemptService;

    public CustomAuthSuccessHandler(LoginAttemptService las) {
        this.loginAttemptService = las;
        // Usar "/" sólo si no hay URL guardada
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {
        // Lógica adicional (reset de contador de fallos)
        loginAttemptService.loginSucceeded(authentication.getName());
        log.info("Login success for user {}", authentication.getName());
        // Luego redirigir a la URL guardada o al defaultTargetUrl
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

