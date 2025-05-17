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

        boolean showCaptcha = false;
        if (loginAttemptService.isCaptchaRequired(user)) {
            log.info("Captcha requerido para el usuario: {}", user);
            showCaptcha = true;
        }
        // Agregar captcha a la URL
        if (showCaptcha) {
            redirectUrl += "&captcha";
        }

        // Redirigir una sola vez
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

