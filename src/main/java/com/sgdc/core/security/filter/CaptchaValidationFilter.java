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

package com.sgdc.core.security.filter;

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
    protected boolean shouldNotFilter(HttpServletRequest req) {
        // Solo filtrar POST a /doLogin
        return !("/doLogin".equals(req.getServletPath())
                && "POST".equalsIgnoreCase(req.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
        // Si no se requiere captcha, continuar
        String username = req.getParameter("username");
        if (loginAttemptService.isCaptchaRequired(username)) {
            log.info("Captcha requerido para el usuario: {}", username);

            String token = req.getParameter("g-recaptcha-response");

            // Falta token voy a login?captcha
            if (token == null || token.isBlank()) {
                res.sendRedirect("/login?captcha");
                return;
            }
            // Token inválido voy a login?captchaError
            if (!captchaService.verify(token)) {
                res.sendRedirect("/login?captchaError");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}

