package com.sgdc.core.sistema.service;

import com.sgdc.core.sistema.domain.Notificacion;
import com.sgdc.core.sistema.repository.NotificacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificacionEnvioServiceImpl implements NotificacionEnvioService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEnvioService.class);

    private final NotificacionRepository notificacionRepo;
    private final JavaMailSender mailSender;
    private final ConfiguracionSistemaService configService;

    // Inyecta aquí, si quieres, el email de origen
    @Value("${spring.mail.username}")
    private String fromAddress;

    public NotificacionEnvioServiceImpl(NotificacionRepository notificacionRepo,
                                        JavaMailSender mailSender,
                                        ConfiguracionSistemaService configService) {
        this.notificacionRepo = notificacionRepo;
        this.mailSender = mailSender;
        this.configService = configService;
    }

    /**
     * Envía todas las notificaciones pendientes de forma asíncrona.
     * Solo se ejecuta si las notificaciones están activadas en la configuración.
     */
    @Async
    @Transactional
    public void enviarNotificacionesPendientes() {
        if (!configService.isNotificacionesActivadas()) {
            log.info("Envio de notificaciones desactivado en configuración");
            return;
        }

        List<Notificacion> pendientes = notificacionRepo.findByEstado("Pendiente");
        log.info("Encontradas {} notificaciones pendientes", pendientes.size());

        pendientes.forEach(this::procesarEnvio);
    }

    /**
     * Para reintento manual de una notificación por su ID.
     */
    @Async
    @Transactional
    public void enviarNotificacion(Integer idNotificacion) {
        Notificacion n = notificacionRepo.findById(idNotificacion)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notificación no encontrada: " + idNotificacion));
        procesarEnvio(n);
    }

    /**
     * Lógica común de envío de un solo registro de notificación.
     */
    private void procesarEnvio(Notificacion n) {
        if (!configService.isNotificacionesActivadas()) {
            log.info("Envio desactivado, no se procesa notificación {}", n.getId());
            return;
        }

        try {
            // Construir el mensaje
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            String destino = n.getPagoMembresia().getMiembro().getCorreoElectronico();
            msg.setTo(destino);
            msg.setSubject("Recordatorio: tu membresía vence pronto");

            // Puedes personalizar el cuerpo
            String text = String.format(
                    "Hola %s,\n\n%s\n\nFecha de vencimiento: %s\n\nSaludos,\nEquipo SGCD",
                    n.getPagoMembresia().getMiembro().getNombre(),
                    n.getMensaje(),
                    n.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            msg.setText(text);

            // Enviar
            log.debug("Enviando notificacion a {} de {}: {}", destino, fromAddress, text);
            mailSender.send(msg);

            // Marcar como enviado
            n.setEstado("Enviada");
            n.setFechaEnvio(LocalDateTime.now());
            log.info("Notificación {} enviada a {}", n.getId(), destino);

        } catch (Exception ex) {
            log.error("Error al enviar notificación {}: {}",
                    n.getId(), ex.getMessage());
            n.setEstado("Error");
        }
        // Guardar el cambio de estado
        notificacionRepo.save(n);
    }

}

