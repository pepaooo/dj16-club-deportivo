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

package com.sgdc.core.sistema.controller;

import com.sgdc.core.sistema.service.NotificacionEnvioService;
import com.sgdc.core.sistema.service.NotificacionService;
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

