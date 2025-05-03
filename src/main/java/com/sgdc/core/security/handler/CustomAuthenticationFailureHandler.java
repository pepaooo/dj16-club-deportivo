package com.sgdc.core.security.handler;

import com.sgdc.core.security.exception.CaptchaException;
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
public class CustomAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String user = request.getParameter("username");
        loginAttemptService.loginFailed(user);

        String redirectUrl = "/login?error";  // credenciales inválidas

        if (exception instanceof CaptchaException) {
            boolean alreadyFailed = loginAttemptService.isCaptchaRequired(user);
            // si falló por captcha requerido (no pusieron token)
            // o por captcha inválido
            redirectUrl = alreadyFailed
                    ? "/login?captcha"     // mostrar el widget
                    : "/login?captchaError";
        } else if (exception instanceof DisabledException) {
            redirectUrl = "/login?disabled";
        } else if (exception instanceof LockedException) {
            redirectUrl = "/login?locked";
        }

        // Redirige al login con el parámetro adecuado
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

