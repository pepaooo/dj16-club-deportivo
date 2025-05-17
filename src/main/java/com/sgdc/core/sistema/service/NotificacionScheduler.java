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

package com.sgdc.core.sistema.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionScheduler {

    private final NotificacionService      notiGen;
    private final NotificacionEnvioService notiSend;

    public NotificacionScheduler(NotificacionService notiGen, NotificacionEnvioService notiSend) {
        this.notiGen = notiGen;
        this.notiSend = notiSend;
    }

    /**
     * Job diario: primero generamos, luego disparamos el envío.
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void runDailyNotificationFlow() {
        notiGen.generarNotificacionesBatch();
        notiSend.enviarNotificacionesPendientes();
    }
}
