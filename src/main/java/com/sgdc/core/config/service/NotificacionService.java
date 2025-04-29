package com.sgdc.core.config.service;

import com.sgdc.core.config.domain.Notificacion;
import com.sgdc.core.config.domain.dto.NotificacionSearchDTO;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NotificacionService {

    void generarNotificacionesBatch();

    Notificacion findById(Integer idNotificacion);

    List<NotificacionSearchDTO> search(String keyword);

}
