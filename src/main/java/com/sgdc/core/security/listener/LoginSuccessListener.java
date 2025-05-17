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

package com.sgdc.core.security.listener;

import com.sgdc.core.actividades.model.Actividad;
import com.sgdc.core.actividades.repository.ActividadRepository;
import com.sgdc.core.security.model.UserPrincipal;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginSuccessListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(LoginSuccessListener.class);

    private final ActividadRepository actividadRepo;
    private final UsuarioRepository usuarioRepo;
    private final HttpServletRequest request;

    public LoginSuccessListener(ActividadRepository actividadRepo, UsuarioRepository usuarioRepo,
                                HttpServletRequest request) {
        this.actividadRepo = actividadRepo;
        this.usuarioRepo = usuarioRepo;
        this.request       = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal up) {
            Actividad act = new Actividad();
            act.setUsuario(up.getUsuario());
            act.setTipoAccion("LOGIN_SUCCESS");
            act.setDescripcion("Inicio de sesión exitoso");
            act.setIpOrigen(extractClientIp());
            act.setUserAgent(request.getHeader("User-Agent"));
            act.setFechaHora(LocalDateTime.now());
            actividadRepo.save(act);
            // Actualizamos el último login de usuario
            Usuario usuario = up.getUsuario();
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepo.save(usuario);
            log.info("Login success. {}", up.getUsuario());
        }
    }

    private String extractClientIp() {
        String xfwd = request.getHeader("X-Forwarded-For");
        if (xfwd != null && !xfwd.isBlank()) {
            return xfwd.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}

