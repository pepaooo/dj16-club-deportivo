package com.sgdc.core.security.service;

import com.sgdc.core.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {

    // TODO : Cambiar a propiedades
    static final int MAX_FAILED_ATTEMPTS = 2; // 5 intentos
    static final int CAPTCHA_THRESHOLD   = 1; // 3 intentos
    static final long LOCK_DURATION_MIN = 2; // 5 minutos

    private final UsuarioRepository usuarioRepo;

    public LoginAttemptService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    public void loginSucceeded(String username) {
        usuarioRepo.findByNombre(username).ifPresent(u -> {
            u.resetFailedAttempts();
            u.setLockTime(null);
            usuarioRepo.save(u);
        });
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
            }
        });
    }
}

