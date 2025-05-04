package com.sgdc.core.security.handler;

import com.sgdc.core.security.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthFailureHandler.class);

    private final LoginAttemptService loginAttemptService;

    public CustomAuthFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String user = request.getParameter("username");
        loginAttemptService.loginFailed(user);
        log.error("Login failure for user {}: {}", user, exception.getClass().getSimpleName());

        String redirectUrl = "/login?error"; // default
        if (exception instanceof DisabledException) {
            redirectUrl = "/login?disabled";
        } else if (exception instanceof LockedException) {
            redirectUrl = "/login?locked";
        }
        // Redirigir una sola vez
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

