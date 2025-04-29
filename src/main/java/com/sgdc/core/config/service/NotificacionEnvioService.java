package com.sgdc.core.config.service;

public interface NotificacionEnvioService {
    void enviarNotificacionesPendientes();
    void enviarNotificacion(Integer idNotificacion);
}
