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

package com.sgdc.core.security.service;

import com.sgdc.core.config.properties.LoginProperties;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private final UsuarioRepository usuarioRepo;
    private final LoginProperties props;

    public LoginAttemptService(UsuarioRepository usuarioRepo, LoginProperties props) {
        this.usuarioRepo = usuarioRepo;
        this.props = props;
    }

    @Transactional
    public UsuarioDTO loginSucceeded(String username) {
        Usuario usuario = usuarioRepo.findByNombre(username).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado: " + username)
        );
        usuario.resetFailedAttempts();
        usuario.setLockTime(null);
        usuarioRepo.save(usuario);
        log.info("El usuario {} ha iniciado sesión correctamente.", username);
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .build();
    }

    @Transactional
    public void loginFailed(String username) {
        usuarioRepo.findByNombre(username).ifPresent(u -> {
            u.incrementFailedAttempt();
            if (u.getFailedAttempt() >= props.getMaxAttempts()) {
                u.lock();
            }
            usuarioRepo.save(u);
        });
    }

    public boolean isCaptchaRequired(String username) {
        return usuarioRepo.findByNombre(username)
                .map(u -> u.getFailedAttempt() >= props.getCaptchaThreshold())
                .orElse(false);
    }

    public boolean isLocked(String username) {
        return usuarioRepo.findByNombre(username)
                .map(u ->
                        u.getFailedAttempt() >= props.getMaxAttempts()
                                && !u.isLockTimeExpired(props.getLockDurationMin())
                )
                .orElse(false);
    }

    @Transactional
    public void unlockIfNeeded(String username) {
        usuarioRepo.findByNombre(username).ifPresent(u -> {
            if (u.getLockTime() != null
                    && u.isLockTimeExpired(props.getLockDurationMin())) {
                u.resetFailedAttempts();
                u.setLockTime(null);
                usuarioRepo.save(u);
                log.info("El usuario {} ha sido desbloqueado.", username);
            }
        });
    }
}

