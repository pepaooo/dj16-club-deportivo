package com.sgdc.core.config.controller;

import com.sgdc.core.config.service.NotificacionEnvioService;
import com.sgdc.core.config.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionRestController {

    private final NotificacionService notificacionService;
    private final NotificacionEnvioService notificacionEnvioService;

    public NotificacionRestController(NotificacionService notificacionService, NotificacionEnvioService notificacionEnvioService) {
        this.notificacionService = notificacionService;
        this.notificacionEnvioService = notificacionEnvioService;
    }

    /**
     * Dispara bajo demanda la generación de notificaciones de vencimiento.
     */
    @PostMapping("/generar")
    public ResponseEntity<String> generarNotificaciones() {
        notificacionService.generarNotificacionesBatch();
        return ResponseEntity.ok("Notificaciones generadas correctamente");
    }

    /**
     * Dispara bajo demanda el envío de notificaciones pendientes.
     */
    @PostMapping("/enviar")
    public ResponseEntity<String> enviarNotificaciones() {
        notificacionEnvioService.enviarNotificacionesPendientes();
        return ResponseEntity.ok("Notificaciones enviadas correctamente");
    }

    @PostMapping("/reenviar/{id}")
    public ResponseEntity<Void> reintentar(@PathVariable Integer id) {
        notificacionEnvioService.enviarNotificacion(id);
        return ResponseEntity.accepted().build();
    }
}

