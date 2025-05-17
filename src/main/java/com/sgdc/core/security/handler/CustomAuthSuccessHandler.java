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

import com.sgdc.core.security.jwt.JWTTokenProvider;
import com.sgdc.core.security.service.LoginAttemptService;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomAuthSuccessHandler
        extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthSuccessHandler.class);

    private final LoginAttemptService loginAttemptService;

    private final JWTTokenProvider jwtTokenProvider;

    public CustomAuthSuccessHandler(LoginAttemptService las, JWTTokenProvider jwtTokenProvider) {
        this.loginAttemptService = las;
        this.jwtTokenProvider = jwtTokenProvider;
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
        log.info("Login success for user {}", authentication.getName());
        UsuarioDTO usuarioDTO = loginAttemptService.loginSucceeded(authentication.getName());

        // Generar el token JWT
        String jwtToken = jwtTokenProvider.generateJwtToken(authentication, usuarioDTO);
        // Guardar el token en una cookie
//        Cookie jwtCookie = new Cookie("token",jwtToken);
//        jwtCookie.setMaxAge(Duration.ofHours(1));
//        jwtCookie.setHttpOnly(true);
//        jwtCookie.setSecure(true);
//        jwtCookie.setPath("/");
//        response.addCookie(jwtCookie);
        ResponseCookie jwtCookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        log.info("JWT token generated and added to cookie");

        // Luego redirigir a la URL guardada o al defaultTargetUrl
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

