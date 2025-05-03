package com.sgdc.core.security.service;

import com.sgdc.core.security.model.UserPrincipal;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, LoginAttemptService loginAttemptService) {
        this.usuarioRepository = usuarioRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // Si está bloqueado pero expiró el tiempo, lo desbloqueamos
        loginAttemptService.unlockIfNeeded(username);

        // Releer tras posible desbloqueo
        if (u.getFailedAttempt() >= LoginAttemptService.MAX_FAILED_ATTEMPTS
                && u.getLockTime() != null
                && !u.isLockTimeExpired(LoginAttemptService.LOCK_DURATION_MIN)) {
            throw new LockedException("Cuenta bloqueada temporalmente");
        }

        return UserPrincipal.build(u);
    }
}
