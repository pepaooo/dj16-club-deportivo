package com.sgdc.core.sistema.service;

import com.sgdc.core.sistema.domain.Notificacion;
import com.sgdc.core.sistema.domain.dto.NotificacionSearchDTO;

import java.util.List;

public interface NotificacionService {

    void generarNotificacionesBatch();

    Notificacion findById(Integer idNotificacion);

    List<NotificacionSearchDTO> search(String keyword);

}
