package com.sgdc.core.security.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    // IP → bucket
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        // 5 tokens cada minuto, refill 5 en 1 minuto
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Solo aplicamos a POST /doLogin
        return !("POST".equalsIgnoreCase(request.getMethod())
                && request.getServletPath().equals("/doLogin"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String ip = req.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            // permite el intento de login
            chain.doFilter(req, res);
        } else {
            // limit exceeded
//            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            res.getWriter().write("Demasiados intentos de login. Intenta de nuevo más tarde.");
            res.sendRedirect("/login?rateLimit");
        }
    }
}

