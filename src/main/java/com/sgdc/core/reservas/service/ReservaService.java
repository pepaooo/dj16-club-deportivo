package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.domain.dto.ReservaDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {

    List<Reserva> findAll();

    Reserva findById(Integer id);

    List<Reserva> search(String keyword);

    ReservaDTO save(ReservaDTO reserva);

//    ReservaDTO update(ReservaDTO reserva);

    void confirmarReserva(Integer id);

    void cancelarReserva(Integer id);

    public List<Reserva> buscarPendientesSolapadas(Integer instalacionId,
                                                   LocalDateTime inicio,
                                                   LocalDateTime fin,
                                                   Integer excludeId);

    List<Reserva> searchReservas(Integer idInstalacion,
                                 Integer idMiembro,
                                 LocalDateTime fechaInicio,
                                 LocalDateTime fechaFin);

    byte[] generatePdfReport(Integer idInstalacion,
                             Integer idMiembro,
                             LocalDateTime fechaInicio,
                             LocalDateTime fechaFin);

}
