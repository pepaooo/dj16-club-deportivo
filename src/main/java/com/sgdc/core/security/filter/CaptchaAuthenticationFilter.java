package com.sgdc.core.security.filter;

import com.sgdc.core.security.exception.CaptchaException;
import com.sgdc.core.security.service.CaptchaService;
import com.sgdc.core.security.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(CaptchaAuthenticationFilter.class);

    private final LoginAttemptService loginAttemptService;
    private final CaptchaService        captchaService;

    public CaptchaAuthenticationFilter(LoginAttemptService loginAttemptService,
                                       CaptchaService captchaService) {
        this.loginAttemptService = loginAttemptService;
        this.captchaService      = captchaService;
        // la URL debe coincidir con tu loginProcessingUrl (por defecto "/login")
        setFilterProcessesUrl("/doLogin");
        setUsernameParameter("username");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

//        String username = obtainUsername(request).trim();
//        log.info("Intentando autenticar usuario: {}", username);

        String username = obtainUsername(request);
        if (username == null) username = "";
        username = username.trim();
        log.info("Intentando autenticar usuario: {}", username);

        // 1) Si ya pasé el umbral, obligo captcha
        if (loginAttemptService.isCaptchaRequired(username)) {
            String token = request.getParameter("g-recaptcha-response");
            if (token == null || token.isBlank()) {
                throw new CaptchaException("Captcha requerido");
            }
            if (!captchaService.verify(token)) {
                throw new CaptchaException("Captcha inválido");
            }
        }

        // 2) Si paso captcha o no lo necesito, delego al padre para auth
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                        username,
                        obtainPassword(request)
                );
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
