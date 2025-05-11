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
