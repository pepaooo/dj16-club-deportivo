package com.sgdc.core.security.service;

import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    @Value("${login.attempts.maxAttempts:5}")
    private int MAX_FAILED_ATTEMPTS;

    @Value("${login.attempts.captchaThreshold:3}")
    private int CAPTCHA_THRESHOLD;

    @Value("${login.attempts.lockDurationMin:5}")
    private long LOCK_DURATION_MIN;

    private final UsuarioRepository usuarioRepo;

    public LoginAttemptService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    public UsuarioDTO loginSucceeded(String username) {
//        usuarioRepo.findByNombre(username).ifPresent(u -> {
//            u.resetFailedAttempts();
//            u.setLockTime(null);
//            usuarioRepo.save(u);
//            log.info("El usuario {} ha iniciado sesión correctamente.", username);
//        });
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
            if (u.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
                u.lock();
            }
            usuarioRepo.save(u);
        });
    }

    public boolean isCaptchaRequired(String username) {
        return usuarioRepo.findByNombre(username)
                .map(u -> u.getFailedAttempt() >= CAPTCHA_THRESHOLD)
                .orElse(false);
    }

    public boolean isLocked(String username) {
        return usuarioRepo.findByNombre(username)
                .map(u ->
                        u.getFailedAttempt() >= MAX_FAILED_ATTEMPTS
                                && !u.isLockTimeExpired(LOCK_DURATION_MIN)
                )
                .orElse(false);
    }

    @Transactional
    public void unlockIfNeeded(String username) {
        usuarioRepo.findByNombre(username).ifPresent(u -> {
            if (u.getLockTime() != null
                    && u.isLockTimeExpired(LOCK_DURATION_MIN)) {
                u.resetFailedAttempts();
                u.setLockTime(null);
                usuarioRepo.save(u);
                log.info("El usuario {} ha sido desbloqueado.", username);
            }
        });
    }
}

