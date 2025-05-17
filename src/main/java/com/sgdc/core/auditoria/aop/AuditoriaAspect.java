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

package com.sgdc.core.auditoria.aop;

import com.sgdc.core.actividades.model.Actividad;
import com.sgdc.core.actividades.repository.ActividadRepository;
import com.sgdc.core.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);
    /**
     * Repositorio de auditoría
     */
    private final ActividadRepository actividadRepo;
    /**
     * Request HTTP
     */
    private final HttpServletRequest request;

    public AuditoriaAspect(ActividadRepository actividadRepo,
                           HttpServletRequest request) {
        this.actividadRepo = actividadRepo;
        this.request = request;
    }

    /**
     * Intercepta métodos marcados con @Auditable tras devolver correctamente.
     */
    @AfterReturning(pointcut = "@annotation(aud)", returning = "result")
    public void logAfter(JoinPoint jp, Auditable aud, Object result) {
        Actividad act = new Actividad();

        // 1) Usuario actual (asumiendo Spring Security)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof UserPrincipal ud) {
            // asume que UserDetails.getUsername() es el ID o token para resolver ID
            act.setUsuario(ud.getUsuario());
        }

        // 2) Tipo de acción y tabla
        act.setTipoAccion(aud.tipoAccion());
        act.setTablaAfectada(aud.tabla());

        // 3) Extraer ID de la entidad afectada via SpEL
        act.setEntidadId(evaluateSpEL(aud.entidadId(), jp, result, Integer.class));

        // 4) Descripción via SpEL (si se ha configurado)
        String desc = aud.descripcion();
        if (!desc.isBlank()) {
            act.setDescripcion(evaluateSpEL(desc, jp, result, String.class));
        }

        // 5) Metadatos HTTP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        act.setIpOrigen(ip);
        act.setUserAgent(StringUtils.abbreviate(request.getHeader("User-Agent"), 255));

        // 6) FechaHora
        act.setFechaHora(LocalDateTime.now());
        // 7) Guardar actividad
        log.info("Guardando actividad: {}", act);
        actividadRepo.save(act);
    }

    /**
     * Evalúa una expresión SpEL en el contexto del JoinPoint
     */
    private <T> T evaluateSpEL(String expression,
                               JoinPoint jp,
                               Object result,
                               Class<T> returnType) {
        MethodSignature sig = (MethodSignature) jp.getSignature();
        Method method = sig.getMethod();
        // Parámetros del metodo
        String[] paramNames = sig.getParameterNames();
        Object[] args = jp.getArgs();

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        // añadir parámetros como variables
        for (int i = 0; i < paramNames.length; i++) {
            ctx.setVariable(paramNames[i], args[i]);
        }
        // variable especial para el resultado
        ctx.setVariable("result", result);

        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(expression).getValue(ctx, returnType);
    }
}

