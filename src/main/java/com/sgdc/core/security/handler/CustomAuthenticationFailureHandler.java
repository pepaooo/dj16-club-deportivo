package com.sgdc.core.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String redirectUrl = "/login?error";  // por defecto: credenciales inválidas

        if (exception instanceof DisabledException) {
            redirectUrl = "/login?disabled";
        } else if (exception instanceof LockedException) {
            redirectUrl = "/login?locked";
        }

        // Redirige al login con el parámetro adecuado
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

