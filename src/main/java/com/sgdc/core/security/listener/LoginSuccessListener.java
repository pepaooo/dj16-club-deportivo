package com.sgdc.core.security.listener;

import com.sgdc.core.audit.model.Actividad;
import com.sgdc.core.audit.repository.ActividadRepository;
import com.sgdc.core.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginSuccessListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    private final ActividadRepository actividadRepo;
    private final HttpServletRequest request;

    public LoginSuccessListener(ActividadRepository actividadRepo,
                                HttpServletRequest request) {
        this.actividadRepo = actividadRepo;
        this.request       = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal up) {
            Actividad act = new Actividad();
            act.setUsuario(up.getUsuario());
            act.setTipoAccion("LOGIN");
            act.setTablaAfectada("usuario");
            act.setEntidadId(up.getUsuario().getId());
            act.setDescripcion("Inicio de sesión exitoso");
            act.setIpOrigen(extractClientIp());
            act.setUserAgent(request.getHeader("User-Agent"));
            act.setFechaHora(LocalDateTime.now());
            actividadRepo.save(act);
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

