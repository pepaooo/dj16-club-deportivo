package com.sgdc.core.security.listener;

import com.sgdc.core.audit.model.Actividad;
import com.sgdc.core.audit.repository.ActividadRepository;
import com.sgdc.core.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginFailureListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static final Logger log = LoggerFactory.getLogger(LoginFailureListener.class);

    private final ActividadRepository actividadRepo;
    private final HttpServletRequest request;

    public LoginFailureListener(ActividadRepository actividadRepo,
                                HttpServletRequest request) {
        this.actividadRepo = actividadRepo;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();

        // TODO Pseudocódigo
//        if (yaGrabéUnFallo(username, last30Min)) {
//            return; // omito duplicados
//        }
//        grabarActividad();


        if (principal instanceof UserPrincipal up) {
            // TODO Este código no es funcional
            Actividad act = new Actividad();
            act.setUsuario(up.getUsuario());
            act.setTipoAccion("LOGIN_FAILURE");
            act.setTablaAfectada("usuario");
            act.setEntidadId(up.getUsuario().getId());
            act.setDescripcion("Falló login para usuario " + up.getUsername());
            act.setIpOrigen(extractClientIp());
            act.setUserAgent(request.getHeader("User-Agent"));
            act.setFechaHora(LocalDateTime.now());
            // si tienes el UserPrincipal cargado, podrías buscar la entidad Usuario y setUsuarioId
            actividadRepo.save(act);
        } else {
            log.error("Login failure. {}", principal);
        }
    }

    private String extractClientIp() {
        String xfwd = request.getHeader("X-Forwarded-For");
        return (xfwd != null && !xfwd.isBlank())
                ? xfwd.split(",")[0]
                : request.getRemoteAddr();
    }
}

