package com.sgdc.core.security.handler;

import com.sgdc.core.security.jwt.JWTTokenProvider;
import com.sgdc.core.security.service.LoginAttemptService;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
        Cookie jwtCookie = new Cookie("token",jwtToken);
        jwtCookie.setMaxAge(Integer.MAX_VALUE);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
        log.info("JWT token generated and added to cookie");

        // Luego redirigir a la URL guardada o al defaultTargetUrl
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

