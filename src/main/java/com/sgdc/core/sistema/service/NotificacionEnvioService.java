package com.sgdc.core.sistema.service;

public interface NotificacionEnvioService {
    void enviarNotificacionesPendientes();
    void enviarNotificacion(Integer idNotificacion);
}
