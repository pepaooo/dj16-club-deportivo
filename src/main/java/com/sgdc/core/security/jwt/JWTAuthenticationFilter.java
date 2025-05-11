package com.sgdc.core.security.jwt;

import com.sgdc.core.security.dto.CredentialsDTO;
import com.sgdc.core.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final JWTTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = extractToken(request, response, filterChain);
        if (jwt == null) return;
        try {
            log.info("JWT token encontrado : {}", jwt);
            if (tokenProvider.validateJwtToken(jwt)) {
                log.info("JWT token válido");
                Claims body = tokenProvider.getClaims(jwt);
                var authorities = (List<Map<String, String>>) body.get("auth");
                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                        .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                        .collect(Collectors.toSet());
                //String username = tokenProvider.getIssuer(jwt);
                String username = tokenProvider.getUserName(jwt);
                // 1) Recuperar UserDetails completo
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                CredentialsDTO credentials = CredentialsDTO.builder()
                        .sub(tokenProvider.getSubject(jwt))
                        .aud(tokenProvider.getAudience(jwt))
                        .exp(tokenProvider.getTokenExpiryFromJWT(jwt).getTime())
                        .iat(tokenProvider.getTokenIatFromJWT(jwt).getTime())
                        .build();
                // 2) Construir Authentication con el UserPrincipal
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, credentials, simpleGrantedAuthorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exception) {
            log.error("No se puede establecer la autenticación del usuario -> Mensaje: {}", exception.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private static String extractToken(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain filterChain) throws IOException, ServletException {
        String jwt = "";
        // 1) Header Authorization
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // 2) Cookie “token”
        if (request.getCookies() != null)
            for (Cookie cookie : request.getCookies())
                if (cookie.getName().equals("token"))
                    jwt = cookie.getValue();
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            log.info("No se ha encontrado el JWT token");
            return null;
        }
        return jwt;
    }
}
